import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaintenanceService } from '../services/maintenance.service';
import { AuthService } from '../../../core/auth/auth';
import { DemandeMaintenanceDTO } from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-maintenance-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './maintenance-detail.html',
  styleUrls: ['./maintenance-detail.scss'],
})
export class MaintenanceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private maintenanceService = inject(MaintenanceService);
  private authService = inject(AuthService);

  retourLink = computed(() => {
    const role = this.authService.currentRole();
    if (role === 'TECHNICIEN') return '/technicien/demandes';
    if (role === 'RESPONSABLE_MAINTENANCE' || role === 'ADMINISTRATEUR') return '/responsable/demandes';
    return '/client/demandes';
  });

  demande = signal<DemandeMaintenanceDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);

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
}