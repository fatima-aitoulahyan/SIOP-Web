import { CommonModule } from '@angular/common';
import { Component, OnDestroy, computed, effect, inject, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth';
import { DashboardService } from '../services/dashboard.service';
import {
  DashboardTechnicienStats,
  PlanningJour,
  ProchaineIntervention,
} from '../../../core/models/dashboard.model';
import {
  BonTravailResumeDTO,
  STATUT_BON_TRAVAIL_LABELS,
  StatutBonTravail,
} from '../../../core/models/bon-travail.model';
import {
  PRIORITE_DEMANDE_LABELS,
  PrioriteDemande,
} from '../../../core/models/maintenance.model';

const TICK_MS = 30_000;

@Component({
  selector: 'app-dashboard-technicien',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-technicien.html',
  styleUrl: './dashboard-technicien.scss',
})
export class DashboardTechnicien implements OnDestroy {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);

  private timer: ReturnType<typeof setInterval> | null = null;

  // ── État UI ──
  chargement = signal(true);
  erreur = signal<string | null>(null);

  // ── Données ──
  prenom = signal('');
  stats = signal<DashboardTechnicienStats | null>(null);
  aujourdhui = signal<BonTravailResumeDTO[]>([]);
  prochaine = signal<ProchaineIntervention | null>(null);
  planning = signal<PlanningJour[]>([]);

  // Horloge qui déclenche le recalcul du compte à rebours
  maintenant = signal(new Date());

  sousTitreAujourdhui = computed(() => {
    const n = this.stats()?.interventionsAujourdhui ?? this.aujourdhui().length;
    if (n === 0) return `Aucune intervention prévue aujourd'hui · ${this.dateDuJour()}`;
    const p = n > 1 ? 's' : '';
    return `${n} intervention${p} prévue${p} aujourd'hui · ${this.dateDuJour()}`;
  });

  constructor() {
    this.timer = setInterval(() => this.maintenant.set(new Date()), TICK_MS);

    // On ne charge qu'une fois l'auth prête (token disponible)
    effect(
      () => {
        if (!this.authService.authReady()) return;
        untracked(() => this.charger());
      },
      { allowSignalWrites: true },
    );
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
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

    this.authService.monProfil().subscribe({
      next: (profil) => this.prenom.set(profil.prenom),
      error: () => {},
    });

    this.dashboardService.getStatsTechnicien()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.stats.set(data),
        error: () => { auMoinsUnEchec = true; },
      });

    this.dashboardService.getInterventionsTechnicienAujourdhui()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.aujourdhui.set(data),
        error: () => { this.aujourdhui.set([]); auMoinsUnEchec = true; },
      });

    this.dashboardService.getProchaineIntervention()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.prochaine.set(data),
        error: () => { this.prochaine.set(null); auMoinsUnEchec = true; },
      });

    this.dashboardService.getPlanningSemaineTechnicien()
      .pipe(finalize(terminer))
      .subscribe({
        next: (data) => this.planning.set(data),
        error: () => { this.planning.set([]); auMoinsUnEchec = true; },
      });
  }

  // ── Compte à rebours (recalculé à chaque tick) ──

  tempsRestant(iv: BonTravailResumeDTO): string {
    const diffMin = this.diffMinutes(iv);
    return diffMin < 0
      ? `En retard de ${this.formatDuree(-diffMin)}`
      : `dans ${this.formatDuree(diffMin)}`;
  }

  estEnRetard(iv: BonTravailResumeDTO): boolean {
    return this.diffMinutes(iv) < 0;
  }

  private diffMinutes(iv: BonTravailResumeDTO): number {
    const cible = new Date(iv.dateInterventionPrevue).getTime();
    return Math.floor((cible - this.maintenant().getTime()) / 60_000);
  }

  private formatDuree(minutes: number): string {
    if (minutes < 60) return `${minutes} min`;
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return m === 0 ? `${h} h` : `${h} h ${m.toString().padStart(2, '0')}`;
  }

  // ── Formatage ──

  formatHeure(value: string): string {
    return new Date(value).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  dateDuJour(): string {
    return this.maintenant().toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  // ── Timeline ──

  estPasseOuEnCours(statut: StatutBonTravail): boolean {
    return statut !== StatutBonTravail.PLANIFIE;
  }

  actionLabel(statut: StatutBonTravail): string {
    return statut === StatutBonTravail.EN_COURS ? 'Continuer' : 'Démarrer';
  }

  // ── Planning semaine ──

  estAujourdhui(jour: PlanningJour): boolean {
    const t = this.maintenant();
    const pad = (n: number) => n.toString().padStart(2, '0');
    return jour.date === `${t.getFullYear()}-${pad(t.getMonth() + 1)}-${pad(t.getDate())}`;
  }

  largeurJour(nombre: number): number {
    const max = Math.max(1, ...this.planning().map((j) => j.nombreInterventions));
    return (nombre / max) * 100;
  }

  // ── Libellés et badges (mêmes conventions que le dashboard Responsable) ──

  statutBonLabel(statut: string): string {
    return STATUT_BON_TRAVAIL_LABELS[statut as StatutBonTravail] ?? statut;
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

  prioriteLabel(priorite: string): string {
    return PRIORITE_DEMANDE_LABELS[priorite as PrioriteDemande] ?? priorite;
  }
}
