import { CommonModule } from '@angular/common';
import { Component, inject, signal, computed, effect, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MaintenanceService } from '../services/maintenance.service';
import { AuthService } from '../../../core/auth/auth';
import { DemandeMaintenanceDTO, StatutDemande, TypeDemande, TYPE_DEMANDE_LABELS } from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-maintenance-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './maintenance-list.html',
  styleUrls: ['./maintenance-list.scss'],
})
export class MaintenanceListComponent {
  private maintenanceService = inject(MaintenanceService);
  private authService = inject(AuthService);

  demandes = signal<DemandeMaintenanceDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  mode = signal<'client' | 'responsable' | 'en-attente' | 'technicien'>('client');
  statutFiltre = signal<StatutDemande | undefined>(undefined);
  typeFiltre = signal<string | undefined>(undefined);

  typesDisponibles = [
    'PANNE',
    'TRAVAUX',
    'PREVENTIVE',
  ];

  titre = computed(() => {
    const modes: Record<string, string> = {
      client: 'Mes demandes de maintenance',
      'en-attente': 'Demandes en attente',
      responsable: 'Toutes les demandes',
      technicien: 'Bons de travail',
    };
    return modes[this.mode()] ?? 'Demandes';
  });

  recherche = signal('');

  demandesFiltrees = computed(() => {
    const terme = this.recherche().trim().toLowerCase();
    const statut = this.statutFiltre();
    const typeD = this.typeFiltre();

    return this.demandes().filter((d) => {
      const matchStatut = statut ? d.statut === statut : true;
      const matchType = typeD ? d.typeDemande === typeD : true;

      const matchTerme =
        !terme ||
        d.id.toString().includes(terme) ||
        d.ascenseurNom?.toLowerCase().includes(terme) ||
        d.clientNom?.toLowerCase().includes(terme) ||
        d.typeDemande?.toLowerCase().includes(terme) ||
        d.description?.toLowerCase().includes(terme);

      return matchStatut && matchType && matchTerme;
    });
  });

  constructor() {
    effect(
      () => {
        if (!this.authService.authReady()) return;
        untracked(() => this.initialiserModeEtCharger());
      },
      { allowSignalWrites: true },
    );
  }

  private initialiserModeEtCharger(): void {
    const role = this.authService.currentRole();
    if (role === 'CLIENT') {
      this.mode.set('client');
    } else if (role === 'RESPONSABLE_MAINTENANCE') {
      this.mode.set('responsable');
    } else if (role === 'TECHNICIEN') {
      this.mode.set('technicien');
    }
    this.charger();
  }

  private charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    const mode = this.mode();

    if (mode === 'client') {
      this.maintenanceService.mesDemandes().subscribe({
        next: (data) => {
          this.demandes.set(data);
          this.chargement.set(false);
        },
        error: () => {
          this.erreur.set('Impossible de charger vos demandes.');
          this.chargement.set(false);
        },
      });
    } else if (mode === 'en-attente') {
      this.maintenanceService.demandesEnAttente().subscribe({
        next: (data) => {
          this.demandes.set(data);
          this.chargement.set(false);
        },
        error: () => {
          this.erreur.set('Impossible de charger les demandes en attente.');
          this.chargement.set(false);
        },
      });
    } else if (mode === 'responsable') {
      const statut = this.statutFiltre();
      const obs = statut
        ? this.maintenanceService.toutesDemandes(statut)
        : this.maintenanceService.toutesDemandes(null);
      obs.subscribe({
        next: (data) => {
          const demandesMaintenanceFiltrees = data.filter((d) => d.typeDemande !== 'EVALUATION');
          this.demandes.set(demandesMaintenanceFiltrees);
          this.chargement.set(false);
        },
        error: () => {
          this.erreur.set('Impossible de charger les demandes.');
          this.chargement.set(false);
        },
      });
    } else if (mode === 'technicien') {
      this.chargement.set(false);
    }
  }

  changerFiltreStatut(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.statutFiltre.set(value === '' ? undefined : (value as StatutDemande));
    // Si on est responsable, on recharge depuis le serveur pour le filtre de statut serveur, sinon le computed gère le client
    if (this.mode() === 'responsable') {
      this.charger();
    }
  }

  changerFiltreType(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.typeFiltre.set(value === '' ? undefined : value);
  }

  typeLabel(type: string): string {
    return TYPE_DEMANDE_LABELS[type as TypeDemande] ?? type;
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

  peutAnnuler(statut: string): boolean {
    return statut === 'EN_ATTENTE' && this.mode() === 'client';
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

  annuler(id: number): void {
    if (!confirm('Annuler cette demande ?')) return;
    this.maintenanceService.annuler(id).subscribe({
      next: () => this.charger(),
      error: () => this.erreur.set("Impossible d'annuler la demande."),
    });
  }
}
