import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth';
import { DashboardService } from '../services/dashboard.service';
import {
  AnomalieCritique,
  DashboardResponsableStats,
} from '../../../core/models/dashboard.model';
import {
  BonTravailResumeDTO,
  STATUT_BON_TRAVAIL_LABELS,
  StatutBonTravail,
} from '../../../core/models/bon-travail.model';
import {
  DemandeMaintenanceDTO,
  PRIORITE_DEMANDE_LABELS,
  PrioriteDemande,
  STATUT_DEMANDE_LABELS,
  StatutDemande,
  TYPE_DEMANDE_LABELS,
  TypeDemande,
} from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-dashboard-responsable-maintenance',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-responsable-maintenance.html',
  styleUrl: './dashboard-responsable-maintenance.scss',
})
export class DashboardResponsableMaintenance {
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
  sousTitreDemandes = computed(() => {
    const n = this.stats()?.enAttente ?? 0;
    if (n === 0) return `Aucune demande en attente · ${this.dateDuJour()}`;
    const p = n > 1 ? 's' : '';
    return `${n} demande${p} en attente · ${this.dateDuJour()}`;
  });

  // ── Données ──
  stats = signal<DashboardResponsableStats | null>(null);
  demandesAtraiter = signal<DemandeMaintenanceDTO[]>([]);
  interventions = signal<BonTravailResumeDTO[]>([]);
  anomalies = signal<AnomalieCritique[]>([]);

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

    let restants = 4;          // 4 appels en parallèle
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

    this.dashboardService.getStats()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.stats.set(data),
        error: () => { auMoinsUnEchec = true; },
      });

    this.dashboardService.getDemandesAtraiter()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.demandesAtraiter.set(data),
        error: () => { this.demandesAtraiter.set([]); auMoinsUnEchec = true; },
      });
      
    this.dashboardService.getInterventionsAujourdhui()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.interventions.set(data),
        error: () => { this.interventions.set([]); auMoinsUnEchec = true; },
      });

    this.dashboardService.getAnomaliesCritiques()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.anomalies.set(data),
        error: () => { this.anomalies.set([]); auMoinsUnEchec = true; },
      });
  }

  // ── Libellés ──
  dateDuJour(): string {
    return new Date().toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  statutLabel(statut: string): string {
    return STATUT_DEMANDE_LABELS[statut as StatutDemande] ?? statut;
  }

  typeLabel(type: string): string {
    return TYPE_DEMANDE_LABELS[type as TypeDemande] ?? type;
  }

  prioriteLabel(priorite: string): string {
    return PRIORITE_DEMANDE_LABELS[priorite as PrioriteDemande] ?? priorite;
  }

  statutBonLabel(statut: string): string {
    return STATUT_BON_TRAVAIL_LABELS[statut as StatutBonTravail] ?? statut;
  }

  // ── Classes CSS des badges ──
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

  statutBonClass(statut: string): string {
    const classes: Record<string, string> = {
      PLANIFIE: 'statut-attente',
      EN_COURS: 'statut-cours',
      TERMINE: 'statut-resolue',
      ANNULE: 'statut-annulee',
    };
    return classes[statut] ?? '';
  }

  prioriteClass(priorite: string): string {
    const classes: Record<string, string> = {
      URGENTE: 'priorite-urgente',
      NORMALE: 'priorite-normale',
      BASSE: 'priorite-basse',
    };
    return classes[priorite] ?? '';
  }
  
  typeClass(type: string): string {
    const classes: Record<string, string> = {
      PANNE: 'type-panne',
      ENTRETIEN_PREVENTIF: 'type-entretien',
      BRUIT_ANORMAL: 'type-bruit',
      EVALUATION: 'type-evaluation',
      AUTRE: 'type-autre',
    };
    return classes[type] ?? 'type-autre';
  }

  // Temps écoulé depuis la création : "il y a 2 h", "hier", "il y a 3 j"…
  tempsEcoule(value: string | null): string {
    if (!value) return '';
    const diffMin = Math.floor((Date.now() - new Date(value).getTime()) / 60_000);
    if (diffMin < 1) return "à l'instant";
    if (diffMin < 60) return `il y a ${diffMin} min`;
    const h = Math.floor(diffMin / 60);
    if (h < 24) return `il y a ${h} h`;
    const j = Math.floor(h / 24);
    if (j === 1) return 'hier';
    if (j < 30) return `il y a ${j} j`;
    return this.formatDate(value);
  }
  
  // ── Formatage ──
  formatDate(value: string | null): string {
    if (!value) return '—';
    return new Date(value).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  }

  formatHeure(value: string): string {
    return new Date(value).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
