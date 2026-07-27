import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';
import {
  DemandeMaintenanceDTO,
  PrioriteDemande,
  PRIORITE_DEMANDE_LABELS,
  StatutDemande,
  STATUT_DEMANDE_LABELS,
  TYPE_DEMANDE_LABELS,
} from '../../../core/models/demande-maintenance.model';
import { DemandeMaintenanceService } from '../services/demande-maintenance.service';

@Component({
  selector: 'app-mes-demandes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mes-demandes.html',
  styleUrl: './mes-demandes.scss',
})
export class MesDemandesComponent implements OnInit {
  private demandeService = inject(DemandeMaintenanceService);
  private authService = inject(AuthService);

  readonly typeLabels = TYPE_DEMANDE_LABELS;
  readonly prioriteLabels = PRIORITE_DEMANDE_LABELS;
  readonly statutLabels = STATUT_DEMANDE_LABELS;
  readonly StatutDemande = StatutDemande;

  demandes = signal<DemandeMaintenanceDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  demandeDetail = signal<DemandeMaintenanceDTO | null>(null);
  demandeAAnnuler = signal<DemandeMaintenanceDTO | null>(null);
  annulationEnCours = signal(false);

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.demandeService.listerMesDemandes().subscribe({
      next: (data) => {
        this.demandes.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set(
          "Impossible de charger vos demandes. Vérifiez votre connexion ou vos droits d'accès.",
        );
        this.chargement.set(false);
      },
    });
  }

  // ---- Classes CSS des badges ----

  classeStatut(statut: StatutDemande): string {
    return 'badge badge-statut-' + statut.toLowerCase().replace(/_/g, '-');
  }

  classePriorite(priorite: PrioriteDemande): string {
    return 'badge badge-priorite-' + priorite.toLowerCase();
  }

  estAnnulable(demande: DemandeMaintenanceDTO): boolean {
    return demande.statut === StatutDemande.EN_ATTENTE;
  }

  // ---- Détail ----

  ouvrirDetail(demande: DemandeMaintenanceDTO): void {
    this.demandeDetail.set(demande);
  }

  fermerDetail(): void {
    this.demandeDetail.set(null);
  }

  // ---- Annulation ----

  demanderAnnulation(demande: DemandeMaintenanceDTO): void {
    this.demandeAAnnuler.set(demande);
  }

  annulerAnnulation(): void {
    this.demandeAAnnuler.set(null);
  }

  confirmerAnnulation(): void {
    const demande = this.demandeAAnnuler();
    if (!demande) return;

    this.annulationEnCours.set(true);
    this.demandeService.annuler(demande.id).subscribe({
      next: (maj) => {
        this.demandes.update((liste) =>
          liste.map((d) => (d.id === maj.id ? maj : d)),
        );
        this.annulationEnCours.set(false);
        this.demandeAAnnuler.set(null);
      },
      error: (err) => {
        this.erreur.set(
          err?.error?.message ??
            "L'annulation a échoué. La demande a peut-être déjà été prise en charge.",
        );
        this.annulationEnCours.set(false);
        this.demandeAAnnuler.set(null);
      },
    });
  }

  deconnexion(): void {
    this.authService.logout();
  }
}
