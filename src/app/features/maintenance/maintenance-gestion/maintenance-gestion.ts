import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MaintenanceService } from '../services/maintenance.service';
import { DemandeMaintenanceDTO } from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-maintenance-gestion',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './maintenance-gestion.html',
  styleUrls: ['./maintenance-gestion.scss'],
})
export class MaintenanceGestionComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private maintenanceService = inject(MaintenanceService);

  demande = signal<DemandeMaintenanceDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  showRejetForm = signal(false);
  envoiEnCours = signal(false);

  rejetForm = this.fb.nonNullable.group({
    motif: ['', [Validators.required, Validators.minLength(10)]],
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.maintenanceService.getDetailPourResponsable(id).subscribe({
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

  accepter(): void {
    // TODO: implémenter l'acceptation quand le backend aura l'endpoint
    alert('Fonctionnalité à implémenter côté backend.');
  }

  rejeter(): void {
    if (this.rejetForm.invalid) {
      this.rejetForm.markAllAsTouched();
      return;
    }

    const d = this.demande();
    if (!d) return;

    this.envoiEnCours.set(true);
    this.maintenanceService.rejeter(d.id, { motif: this.rejetForm.getRawValue().motif }).subscribe({
      next: () => this.ngOnInit(),
      error: () => {
        this.erreur.set('Échec du rejet.');
        this.envoiEnCours.set(false);
      },
    });
  }
  openPhoto(url: string): void {
    window.open(url, '_blank');
  }
}