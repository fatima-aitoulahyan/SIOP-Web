import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SiteDTO } from '../../../core/models/site.model';
import { SiteService } from '../services/site';

@Component({
  selector: 'app-site-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './site-list.html',
  styleUrls: ['../site-theme.css', './site-list.scss'],
})
export class SiteListComponent implements OnInit {
  private siteService = inject(SiteService);

  sites = signal<SiteDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  siteASupprimer = signal<SiteDTO | null>(null);
  suppressionEnCours = signal(false);

  // ---- Recherche / filtre ----
  recherche = signal('');
  filtreVille = signal<string>('TOUTES');

  villesDisponibles = computed(() => {
    const noms = this.sites()
      .map((s) => s.villeNom)
      .filter(Boolean);
    return Array.from(new Set(noms)).sort();
  });

  sitesFiltres = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    const ville = this.filtreVille();

    return this.sites().filter((s) => {
      const matchVille = ville === 'TOUTES' || s.villeNom === ville;
      if (!matchVille) return false;

      if (!terme) return true;

      return (
        s.adresse?.toLowerCase().includes(terme) ||
        s.villeNom?.toLowerCase().includes(terme) ||
        s.clientNom?.toLowerCase().includes(terme) ||
        s.parcNom?.toLowerCase().includes(terme)
      );
    });
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.siteService.listerTous().subscribe({
      next: (data) => {
        this.sites.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set(
          "Impossible de charger la liste des sites. Vérifiez votre connexion ou vos droits d'accès.",
        );
        this.chargement.set(false);
      },
    });
  }

  demanderSuppression(site: SiteDTO): void {
    this.siteASupprimer.set(site);
  }

  annulerSuppression(): void {
    this.siteASupprimer.set(null);
  }

  confirmerSuppression(): void {
    const site = this.siteASupprimer();
    if (!site) return;

    this.suppressionEnCours.set(true);
    this.siteService.supprimer(site.id).subscribe({
      next: () => {
        this.sites.update((liste) => liste.filter((s) => s.id !== site.id));
        this.suppressionEnCours.set(false);
        this.siteASupprimer.set(null);
      },
      error: () => {
        this.erreur.set(
          'La suppression a échoué. Le site contient peut-être encore des ascenseurs.',
        );
        this.suppressionEnCours.set(false);
        this.siteASupprimer.set(null);
      },
    });
  }
}
