import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UtilisateurService } from '../services/utilisateur';
import {
  UtilisateurResponseDTO,
  TYPE_UTILISATEUR_LABELS,
} from '../../../core/models/utilisateur.model';

@Component({
  selector: 'app-utilisateur-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './utilisateur-list.html',
  styleUrl: './utilisateur-list.scss',
})
export class UtilisateurList implements OnInit {
  private utilisateurService = inject(UtilisateurService);

  utilisateurs = signal<UtilisateurResponseDTO[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  labels = TYPE_UTILISATEUR_LABELS;

  // ---- Recherche / filtre ----
  recherche = signal('');
  filtreType = signal<string>('TOUS');
  filtreStatut = signal<'TOUS' | 'ACTIF' | 'INACTIF'>('TOUS');

  typesDisponibles = computed(() => {
    const types = this.utilisateurs()
      .map((u) => u.type)
      .filter(Boolean);
    return Array.from(new Set(types));
  });

  utilisateursFiltres = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    const type = this.filtreType();
    const statut = this.filtreStatut();

    return this.utilisateurs().filter((u) => {
      const matchType = type === 'TOUS' || u.type === type;
      if (!matchType) return false;

      const matchStatut =
        statut === 'TOUS' || (statut === 'ACTIF' && u.actif) || (statut === 'INACTIF' && !u.actif);
      if (!matchStatut) return false;

      if (!terme) return true;

      return (
        u.nom?.toLowerCase().includes(terme) ||
        u.prenom?.toLowerCase().includes(terme) ||
        u.email?.toLowerCase().includes(terme)
      );
    });
  });

  ngOnInit() {
    this.charger();
  }

  charger() {
    this.loading.set(true);
    this.utilisateurService.getAll().subscribe({
      next: (data) => {
        this.utilisateurs.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message || 'Erreur de chargement.');
        this.loading.set(false);
      },
    });
  }

  desactiver(id: number) {
    if (!confirm('Désactiver cet utilisateur ?')) return;
    this.utilisateurService.desactiver(id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage.set(err?.error?.message || 'Erreur.'),
    });
  }

  supprimer(id: number) {
    if (!confirm('Supprimer définitivement cet utilisateur ?')) return;
    this.utilisateurService.supprimer(id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage.set(err?.error?.message || 'Erreur.'),
    });
  }
}
