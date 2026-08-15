import { CommonModule } from '@angular/common';
import { Component, inject, signal, computed, effect, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';
import { AscenseurService } from '../../ascenseurs/services/ascenseur.service';
import { MaintenanceService } from '../../maintenance/services/maintenance.service';
import { EvaluationAscenseurService } from '../../maintenance/services/evaluation-ascenseur.service';
import {
  TypeDemande,
  StatutDemande,
  DemandeMaintenanceDTO,
  STATUT_DEMANDE_LABELS,
  TYPE_DEMANDE_LABELS,
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
  private ascenseurService = inject(AscenseurService);
  private maintenanceService = inject(MaintenanceService);
  private evaluationService = inject(EvaluationAscenseurService);

  chargement = signal(true);
  erreur = signal<string | null>(null);

  nbAscenseurs = signal(0);
  nbEvaluations = signal(0);
  demandes = signal<DemandeMaintenanceDTO[]>([]);

  bienvenue = computed(() => {
    const role = this.authService.currentRole();
    return role === 'CLIENT' ? 'Client' : 'Votre espace';
  });

  demandesToutes = computed(() => this.demandes());
  nbDemandes = computed(() => this.demandesToutes().length);
  nbEnAttente = computed(
    () => this.demandesToutes().filter((d) => d.statut === StatutDemande.EN_ATTENTE).length,
  );
  nbEnCours = computed(
    () =>
      this.demandesToutes().filter(
        (d) => d.statut === StatutDemande.ASSIGNEE || d.statut === StatutDemande.EN_COURS,
      ).length,
  );
  nbResolues = computed(
    () => this.demandesToutes().filter((d) => d.statut === StatutDemande.RESOLUE).length,
  );

  dernieresDemandes = computed(() =>
    [...this.demandes()]
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5),
  );

  constructor() {
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
    const terminer = () => {
      restants -= 1;
      if (restants === 0) this.chargement.set(false);
    };

    this.ascenseurService.mesAscenseurs().subscribe({
      next: (res) => this.nbAscenseurs.set(res.data.length),
      error: () => this.nbAscenseurs.set(0),
      complete: terminer,
    });

    this.maintenanceService.mesDemandes().subscribe({
      next: (data) => this.demandes.set(data),
      error: () => this.demandes.set([]),
      complete: terminer,
    });

    this.evaluationService.mesEvaluations().subscribe({
      next: (data) => this.nbEvaluations.set(data.length),
      error: () => this.nbEvaluations.set(0),
      complete: terminer,
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

  formatDate(value: string): string {
    return new Date(value).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  }
}
