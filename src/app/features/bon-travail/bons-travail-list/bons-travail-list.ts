import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth';
import { BonTravailService } from '../services/bon-travail.service';
import { BonTravailResumeDTO, StatutBonTravail } from '../../../core/models/bon-travail.model';
import { PrioriteDemande } from '../../../core/models/maintenance.model';

@Component({
  selector: 'app-bons-travail-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './bons-travail-list.html',
  styleUrls: ['./bons-travail-list.scss'],
})
export class BonsTravailListComponent {
  private bonTravailService = inject(BonTravailService);
  private authService = inject(AuthService);

  bonsTravaux = signal<BonTravailResumeDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  recherche = signal('');
  statutFiltre = signal<StatutBonTravail | undefined>(undefined);
  annulationEnCours = signal<number | null>(null);

  mode = computed<'responsable' | 'technicien'>(
    () => (this.authService.currentRole() === 'RESPONSABLE_MAINTENANCE' ? 'responsable' : 'technicien'),
  );

  titre = computed(() => (this.mode() === 'responsable' ? 'Bons de travail' : 'Mes interventions'));

  statuts = Object.values(StatutBonTravail);

  bonsTravauxFiltres = computed(() => {
    let list = this.bonsTravaux();
    const statut = this.statutFiltre();
    if (statut) list = list.filter((b) => b.statut === statut);
    const terme = this.recherche().trim().toLowerCase();
    if (!terme) return list;
    return list.filter(
      (b) =>
        b.ascenseurNom?.toLowerCase().includes(terme) ||
        b.siteAdresse?.toLowerCase().includes(terme) ||
        b.parcNom?.toLowerCase().includes(terme) ||
        b.technicienResponsableNom?.toLowerCase().includes(terme),
    );
  });

  constructor() {
    effect(() => {
      if (!this.authService.authReady()) return;
      untracked(() => this.charger());
    }, { allowSignalWrites: true });
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    const source =
      this.mode() === 'responsable'
        ? this.bonTravailService.lister()
        : this.bonTravailService.mesInterventions();
    source.subscribe({
      next: (data) => {
        this.bonsTravaux.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les bons de travail.');
        this.chargement.set(false);
      },
    });
  }

  changerFiltreStatut(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.statutFiltre.set(value === '' ? undefined : (value as StatutBonTravail));
    this.charger();
  }

  statutClass(statut: string): string {
    const classes: Record<string, string> = {
      PLANIFIE: 'statut-planifie',
      EN_COURS: 'statut-cours',
      TERMINE: 'statut-termine',
      ANNULE: 'statut-annule',
    };
    return classes[statut] ?? '';
  }

  statutLabel(statut: string): string {
    const labels: Record<string, string> = {
      PLANIFIE: 'Planifié',
      EN_COURS: 'En cours',
      TERMINE: 'Terminé',
      ANNULE: 'Annulé',
    };
    return labels[statut] ?? statut;
  }

  prioriteLabel(priorite: string): string {
    const labels: Record<string, string> = {
      BASSE: 'Basse',
      NORMALE: 'Normale',
      URGENTE: 'Urgente',
    };
    return labels[priorite] ?? priorite;
  }

  prioriteClass(priorite: string): string {
    const classes: Record<string, string> = {
      BASSE: 'priorite-basse',
      NORMALE: 'priorite-normale',
      URGENTE: 'priorite-urgente',
    };
    return classes[priorite] ?? '';
  }

  detailLien(b: BonTravailResumeDTO): (string | number)[] {
    return this.mode() === 'responsable'
      ? ['/responsable/bons-travail', b.id]
      : ['/technicien/bons-travail', b.id];
  }

  peutAnnuler(b: BonTravailResumeDTO): boolean {
    return (
      this.mode() === 'responsable' &&
      b.statut !== 'TERMINE' &&
      b.statut !== 'ANNULE'
    );
  }

  annuler(b: BonTravailResumeDTO): void {
    if (!confirm(`Annuler le bon de travail #${b.id} ?`)) return;
    this.annulationEnCours.set(b.id);
    this.bonTravailService.annuler(b.id).subscribe({
      next: () => {
        this.annulationEnCours.set(null);
        this.charger();
      },
      error: (err) => {
        this.annulationEnCours.set(null);
        this.erreur.set(err?.error?.message ?? "Impossible d'annuler le bon de travail.");
      },
    });
  }
}