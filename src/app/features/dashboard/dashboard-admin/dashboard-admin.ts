import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth';
import { DashboardService } from '../services/dashboard.service';
import {
  ActiviteParParc,
  DashboardAdminStats,
  RepartitionUtilisateurs,
} from '../../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.scss',
})
export class DashboardAdmin {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);

  // ── État UI ──
  chargement = signal(true);
  erreur = signal<string | null>(null);

  // ── Profil / en-tête ──
  prenom = signal('');
  bonjour = computed(() =>
    this.prenom() ? `Bonjour ${this.prenom()} 👋` : 'Bienvenue 👋',
  );

  // ── Données ──
  stats = signal<DashboardAdminStats | null>(null);
  repartition = signal<RepartitionUtilisateurs | null>(null);
  activite = signal<ActiviteParParc[]>([]);

  constructor() {
    // On ne charge qu'une fois l'auth prête (token disponible)
    effect(
      () => {
        if (!this.authService.authReady()) return;
        untracked(() => this.charger());
      },
      { allowSignalWrites: true },
    );
  }

  private charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    let restants = 3;
    let auMoinsUnEchec = false;

    const terminer = () => {
      restants -= 1;
      if (restants === 0) {
        this.chargement.set(false);
        if (auMoinsUnEchec) {
          this.erreur.set("Certaines données n'ont pas pu être chargées.");
        }
      }
    };

    // Ne participe pas au compteur : non bloquant
    this.authService.monProfil().subscribe({
      next: (profil) => this.prenom.set(profil.prenom),
      error: () => {},
    });

    this.dashboardService.getStatsAdmin()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.stats.set(data),
        error: () => { auMoinsUnEchec = true; },
      });

    this.dashboardService.getRepartitionUtilisateurs()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.repartition.set(data),
        error: () => { auMoinsUnEchec = true; },
      });

    this.dashboardService.getActiviteParParc()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.activite.set(data),
        error: () => { this.activite.set([]); auMoinsUnEchec = true; },
      });
  }

  dateDuJour(): string {
    return new Date().toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  // Largeur d'une barre de répartition (% de la valeur maximale)
  largeurBarre(valeur: number): number {
    const r = this.repartition();
    if (!r) return 0;
    const max = Math.max(r.clients, r.techniciens, r.responsables, r.administrateurs);
    return max === 0 ? 0 : (valeur / max) * 100;
  }

  // Largeur de la mini-barre "demandes en attente" du tableau par parc
  largeurDemandes(nombre: number): number {
    const max = Math.max(0, ...this.activite().map((a) => a.demandesEnAttente));
    return max === 0 ? 0 : (nombre / max) * 100;
  }

  sansTechnicien(parc: ActiviteParParc): boolean {
    return parc.nombreTechniciens === 0;
  }

  formatTaux(taux: number): string {
    return `${taux.toLocaleString('fr-FR', { maximumFractionDigits: 1 })} %`;
  }
}
