import { CommonModule } from '@angular/common';
import { Component, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  DemandeMaintenanceDTO,
  TypeDemande,
} from '../../../../core/models/demande-maintenance.model';
import { MaintenanceService } from '../../services/maintenance.service';

@Component({
  selector: 'app-evaluations-demandes-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './evaluations-demandes-list.html',
  styleUrls: ['./evaluations-demandes-list.scss'],
})
export class EvaluationsDemandesListComponent {
  private maintenanceService = inject(MaintenanceService);

  demandes = signal<DemandeMaintenanceDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  recherche = signal('');

  // Filtrer uniquement celles qui ont le type EVALUATION en utilisant l'Enum
  demandesFiltrees = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    return this.demandes().filter((d) => {
      // On compare avec TypeDemande.EVALUATION ou on utilise un cast si l'enum a une autre structure
      const matchType = d.typeDemande === TypeDemande.EVALUATION;

      const matchTerme =
        !terme ||
        d.id.toString().includes(terme) ||
        d.createdAt.toString().includes(terme) ||
        d.ascenseurNom?.toLowerCase().includes(terme) ||
        d.clientNom?.toLowerCase().includes(terme) ||
        d.description?.toLowerCase().includes(terme);

      return matchType && matchTerme;
    });
  });

  constructor() {
    this.chargerEvaluations();
  }

  private chargerEvaluations(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    // On récupère toutes les demandes et on ne garde que les EVALUATION
    this.maintenanceService.toutesDemandes(null).subscribe({
      next: (data) => {
        const evaluations = data.filter((d) => d.typeDemande === TypeDemande.EVALUATION);

        this.demandes.set(evaluations);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set(`Impossible de charger les demandes d'évaluation.`);
        this.chargement.set(false);
      },
    });
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

  statutLabel(statut: string): string {
    const labels: Record<string, string> = {
      EN_ATTENTE: 'En attente',
      ASSIGNEE: 'Assignée',
      EN_COURS: 'En cours',
      RESOLUE: 'Résolue',
      ANNULEE: 'Annulée',
      REJETEE: 'Rejetée',
    };
    return labels[statut] ?? statut;
  }
}
