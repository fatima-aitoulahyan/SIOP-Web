import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth';
import { DashboardService } from '../services/dashboard.service';
import { MaintenanceService } from '../../maintenance/services/maintenance.service';
import {
  AscenseurAvecEtat,
  DashboardClientStats,
  DemandeSuivi,
} from '../../../core/models/dashboard.model';
import {
  DemandeMaintenanceDTO,
  STATUT_DEMANDE_LABELS,
  StatutDemande,
  TYPE_DEMANDE_LABELS,
  TypeDemande,
} from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-dashboard-client',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-client.html',
  styleUrl: './dashboard-client.scss',
})
export class DashboardClient {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private maintenanceService = inject(MaintenanceService);

  // ── État UI ──
  chargement = signal(true);
  erreur = signal<string | null>(null);

  // ── Données ──
  prenom = signal('');
  stats = signal<DashboardClientStats | null>(null);
  suivi = signal<DemandeSuivi[]>([]);
  ascenseurs = signal<AscenseurAvecEtat[]>([]);
  demandes = signal<DemandeMaintenanceDTO[]>([]); // source de l'historique

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

    let restants = 4;
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

    this.dashboardService.getStatsClient()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.stats.set(data),
        error: () => { auMoinsUnEchec = true; },
      });

    this.dashboardService.getSuiviClient()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.suivi.set(data),
        error: () => { this.suivi.set([]); auMoinsUnEchec = true; },
      });

    this.dashboardService.getAscenseursAvecEtat()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.ascenseurs.set(data),
        error: () => { this.ascenseurs.set([]); auMoinsUnEchec = true; },
      });

    // Historique : on charge tout puis on filtre côté composant
    this.maintenanceService.mesDemandes()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.demandes.set(data),
        error: () => { this.demandes.set([]); auMoinsUnEchec = true; },
      });
  }

  // ── Données dérivées ──

  bonjour = computed(() =>
    this.prenom() ? `Bonjour ${this.prenom()} 👋` : 'Bienvenue 👋',
  );

  private readonly ordreStatut: Record<string, number> = {
    EN_COURS: 0,
    ASSIGNEE: 1,
    EN_ATTENTE: 2,
  };

  private dateTs(value: string | null | undefined): number {
    return value ? new Date(value).getTime() : 0;
  }

  // Suivi : les plus avancées d'abord, puis par date décroissante
  suiviTrie = computed(() =>
    [...this.suivi()].sort((a, b) => {
      const diff =
        (this.ordreStatut[a.statut] ?? 9) - (this.ordreStatut[b.statut] ?? 9);
      return diff !== 0 ? diff : this.dateTs(b.dateDemande) - this.dateTs(a.dateDemande);
    }),
  );

  // Ascenseurs : ceux qui ont besoin d'attention en premier
  ascenseursTries = computed(() =>
    [...this.ascenseurs()].sort((a, b) => {
      if (a.aDemandeActive !== b.aDemandeActive) return a.aDemandeActive ? -1 : 1;
      return a.nom.localeCompare(b.nom);
    }),
  );

  // 5 dernières demandes closes (résolues, annulées ou rejetées), triées par date de clôture
  historique = computed(() =>
    this.demandes()
      .filter(
        (d) =>
          d.statut === StatutDemande.RESOLUE ||
          d.statut === StatutDemande.ANNULEE ||
          d.statut === StatutDemande.REJETEE,
      )
      .sort(
        (a, b) =>
          this.dateTs(b.dateResolution ?? b.createdAt) -
          this.dateTs(a.dateResolution ?? a.createdAt),
      )
      .slice(0, 5),
  );

  // ── Formatage / libellés ──

  dateDuJour(): string {
    return new Date().toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  formatJour(value: string | null): string {
    if (!value) return '';
    return new Date(value).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
    });
  }

  formatDate(value: string): string {
    return new Date(value).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  }

  statutLabel(statut: string): string {
    return STATUT_DEMANDE_LABELS[statut as StatutDemande] ?? statut;
  }

  statutClass(statut: string): string {
    const classes: Record<string, string> = {
      EN_ATTENTE: 'statut-attente',
      ASSIGNEE: 'statut-assignee',
      EN_COURS: 'statut-cours',
      RESOLUE: 'statut-resolue',
      ANNULEE: 'statut-annulee',
      REJETEE: 'statut-rejetee',
    };
    return classes[statut] ?? '';
  }

  typeLabel(type: string): string {
    return TYPE_DEMANDE_LABELS[type as TypeDemande] ?? type;
  }

  // État affiché sur la ligne d'un ascenseur (Section 3)
  etatLabel(a: AscenseurAvecEtat): string {
    return a.aDemandeActive && a.statutDemande
      ? this.statutLabel(a.statutDemande)
      : 'Tout va bien';
  }

  etatClass(a: AscenseurAvecEtat): string {
    if (!a.aDemandeActive || !a.statutDemande) return 'etat-ok';
    const classes: Record<string, string> = {
      EN_ATTENTE: 'statut-attente',
      ASSIGNEE: 'statut-assignee',
      EN_COURS: 'statut-cours',
    };
    return classes[a.statutDemande] ?? 'etat-ok';
  }
}
