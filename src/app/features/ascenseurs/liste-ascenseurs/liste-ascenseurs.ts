import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AscenseurService } from '../services/ascenseur.service';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';

@Component({
  selector: 'app-liste-ascenseurs',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './liste-ascenseurs.html',
  styleUrls: ['./liste-ascenseurs.scss'],
})
export class ListeAscenseurs implements OnInit {
  private ascenseurService = inject(AscenseurService);

  ascenseurs = signal<AscenseurDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  suppressionEnCours = signal<number | null>(null);

  recherche = signal('');
  filtreStatut = signal<'TOUS' | 'ACTIF' | 'INACTIF'>('TOUS');
  statutEnCours = signal<number | null>(null);

  ascenseursFiltres = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    const statut = this.filtreStatut();

    return this.ascenseurs().filter((a) => {
      const matchStatut =
        statut === 'TOUS' || (statut === 'ACTIF' && a.actif) || (statut === 'INACTIF' && !a.actif);

      if (!matchStatut) return false;
      if (!terme) return true;

      const clientTexte = [a.clientPrenom, a.clientNom, a.clientNomEntreprise]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return (
        a.nom?.toLowerCase().includes(terme) ||
        a.fabricant?.toLowerCase().includes(terme) ||
        clientTexte.includes(terme) ||
        a.siteAdresse?.toLowerCase().includes(terme)
      );
    });
  });
  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    this.ascenseurService.listerTous().subscribe({
      next: (res) => {
        this.ascenseurs.set(res.data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger la liste des ascenseurs.');
        this.chargement.set(false);
      },
    });
  }

  supprimer(ascenseur: AscenseurDTO): void {
    const confirmation = confirm(
      `Voulez-vous vraiment supprimer l'ascenseur "${ascenseur.nom}" ? Cette action est irréversible.`,
    );
    if (!confirmation) {
      return;
    }

    this.suppressionEnCours.set(ascenseur.id);
    this.erreur.set(null);

    this.ascenseurService.supprimer(ascenseur.id).subscribe({
      next: () => {
        this.ascenseurs.update((liste) => liste.filter((a) => a.id !== ascenseur.id));
        this.suppressionEnCours.set(null);
      },
      error: () => {
        this.erreur.set(`Impossible de supprimer l'ascenseur "${ascenseur.nom}".`);
        this.suppressionEnCours.set(null);
      },
    });
  }

  basculerStatut(ascenseur: AscenseurDTO): void {
    this.statutEnCours.set(ascenseur.id);
    this.erreur.set(null);

    this.ascenseurService.basculerStatut(ascenseur.id).subscribe({
      next: (res) => {
        this.ascenseurs.update((liste) => liste.map((a) => (a.id === ascenseur.id ? res.data : a)));
        this.statutEnCours.set(null);
      },
      error: () => {
        this.erreur.set(`Impossible de changer le statut de "${ascenseur.nom}".`);
        this.statutEnCours.set(null);
      },
    });
  }
}
