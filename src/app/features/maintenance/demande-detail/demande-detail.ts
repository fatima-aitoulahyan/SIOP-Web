import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaintenanceService } from '../services/maintenance.service';
import { AuthService } from '../../../core/auth/auth';
import { DemandeMaintenanceDTO } from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-demande-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './demande-detail.html',
  styleUrls: ['./demande-detail.scss'],
})
export class DemandeDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private maintenanceService = inject(MaintenanceService);
  private authService = inject(AuthService);

  retourLink = computed(() => {
    const role = this.authService.currentRole();
    if (role === 'TECHNICIEN') return '/technicien/demandes';
    if (role === 'RESPONSABLE_MAINTENANCE' || role === 'ADMINISTRATEUR')
      return '/responsable/demandes';
    return '/client/demandes';
  });

  demande = signal<DemandeMaintenanceDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  // Dictionnaires de libellés
  typeLabels: Record<string, string> = {
    PANNE: 'Panne',
    TRAVAUX: 'Travaux',
    PREVENTIVE: 'Maintenance préventive',
    EVALUATION: "Évaluation d'un nouvel ascenseur",
  };

  statutLabels: Record<string, string> = {
    EN_ATTENTE: 'En attente',
    ASSIGNEE: 'Assignée',
    EN_COURS: 'En cours',
    RESOLUE: 'Résolue',
    ANNULEE: 'Annulée',
  };

  prioriteLabels: Record<string, string> = {
    BASSE: 'Basse',
    NORMALE: 'Normale',
    URGENTE: 'Urgente',
  };

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.maintenanceService.getDetail(id).subscribe({
      next: (data) => {
        this.demande.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger la demande.');
        this.chargement.set(false);
      },
    });
  }

  annuler(): void {
    const d = this.demande();
    if (!d || d.statut !== 'EN_ATTENTE') return;
    if (!confirm('Annuler cette demande ?')) return;

    this.maintenanceService.annuler(d.id).subscribe({
      next: () => this.ngOnInit(),
      error: () => this.erreur.set("Impossible d'annuler la demande."),
    });
  }

  openPhoto(url: string): void {
    window.open(url, '_blank');
  }

  // Méthode manquante pour vérifier s'il y a des images
  aDesImages(photos: any[]): boolean {
    return photos?.some((p) => p.typeFichier === 'IMAGE') ?? false;
  }

  // Classes dynamiques pour les statuts
  classeStatut(statut: string): string {
    const map: Record<string, string> = {
      EN_ATTENTE: 'badge-statut-en-attente',
      ASSIGNEE: 'badge-statut-assignee',
      EN_COURS: 'badge-statut-en-cours',
      RESOLUE: 'badge-statut-resolue',
      ANNULEE: 'badge-statut-annulee',
    };
    return `badge ${map[statut] || ''}`;
  }

  // Classes dynamiques pour les priorités
  classePriorite(priorite: string): string {
    const map: Record<string, string> = {
      BASSE: 'badge-priorite-basse',
      NORMALE: 'badge-priorite-normale',
      URGENTE: 'badge-priorite-urgente',
    };
    return map[priorite] || '';
  }
}
