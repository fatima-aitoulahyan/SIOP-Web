import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';
import { BonTravailCreateDTO } from '../../../core/models/bon-travail.model';
import { PrioriteDemande, DemandeMaintenanceDTO } from '../../../core/models/maintenance.model';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';
import { TechnicienResumeDTO } from '../../../core/models/technicien.model';
import { SiteDTO } from '../../../core/models/site.model';
import { BonTravailService } from '../services/bon-travail.service';
import { MaintenanceService } from '../../maintenance/services/maintenance.service';
import { AscenseurService } from '../../ascenseurs/services/ascenseur.service';
import { TechnicienService } from '../../../core/services/technicien.service';
import { SiteService } from '../../sites/services/site';
import { SiteFormComponent } from '../../sites/site-form/site-form';

@Component({
  selector: 'app-bons-travail-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, SiteFormComponent],
  templateUrl: './bons-travail-form.html',
  styleUrls: ['./bons-travail-form.scss'],
})
export class BonsTravailFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private bonTravailService = inject(BonTravailService);
  private maintenanceService = inject(MaintenanceService);
  private ascenseurService = inject(AscenseurService);
  private technicienService = inject(TechnicienService);
  private siteService = inject(SiteService);

  origine = signal<'demande' | 'ascenseur'>('demande');
  demandePrechargee = signal<DemandeMaintenanceDTO | null>(null);
  chargementListes = signal(true);
  erreurListes = signal<string | null>(null);
  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);

  // Gestion de l'affichage du formulaire de site en superposition
  necessiteCreationSite = signal(false);

  // Gestion du choix entre sites existants du client (cas ÉVALUATION)
  sitesClientExistants = signal<SiteDTO[]>([]);
  necessiteChoixSite = signal(false);

  // Stockage du parc sélectionné ou créé lors d'une demande d'évaluation
  parcSelectionneId = signal<number | null>(null);

  iaEnCours = signal(false);
  iaErreur = signal<string | null>(null);
  iaGeneree = signal(false);

  demandes = signal<DemandeMaintenanceDTO[]>([]);
  ascenseurs = signal<AscenseurDTO[]>([]);
  techniciensParc = signal<TechnicienResumeDTO[]>([]);
  erreurTechniciens = signal<string | null>(null);

  renfortIds = signal<number[]>([]);
  responsableSelectionne = signal<number | null>(null);

  siteSelectionneId = signal<number | null>(null);

  priorites = Object.values(PrioriteDemande);

  form = this.fb.nonNullable.group({
    demandeMaintenanceId: [null as number | null, [Validators.required]],
    ascenseurId: [null as number | null],
    technicienResponsableId: [null as number | null, [Validators.required]],
    dateInterventionPrevue: ['', [Validators.required]],
    dureeEstimeeMinutes: [60, [Validators.required, Validators.min(1)]],
    priorite: [PrioriteDemande.NORMALE as PrioriteDemande],
    description: [''],
    visitePreventive: [false],
  });

  techniciensRenfortDisponibles = computed(() => {
    const resp = this.responsableSelectionne();
    return this.techniciensParc().filter((t) => Number(t.id) !== resp);
  });

  get demandeSelectionnee(): number | null {
    const v = this.form.get('demandeMaintenanceId')?.value;
    return v ? Number(v) : null;
  }

  onChangerResponsable(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.responsableSelectionne.set(value ? Number(value) : null);
    this.renfortIds.set(this.renfortIds().filter((id) => id !== Number(value)));
  }

  constructor() {
    this.form.get('ascenseurId')!.valueChanges.subscribe((id) => {
      if (this.origine() !== 'ascenseur') return;
      if (id != null) this.chargerTechniciensParc(Number(id));
      else this.techniciensParc.set([]);
    });

    effect(
      () => {
        if (!this.authService.authReady()) return;
        untracked(() => {
          this.chargerListes();
          this.initDepuisRequete();
        });
      },
      { allowSignalWrites: true },
    );
  }

  private initDepuisRequete(): void {
    const idParam = this.route.snapshot.queryParamMap.get('demande');
    if (!idParam) return;
    const id = Number(idParam);
    if (!Number.isInteger(id)) {
      this.erreurListes.set('Identifiant de demande invalide.');
      return;
    }
    this.maintenanceService.getDetailPourResponsable(id).subscribe({
      next: (d) => {
        this.demandePrechargee.set(d);
        this.passerEnModeDemande();
        this.form.get('demandeMaintenanceId')!.setValue(d.id);
        this.form.get('visitePreventive')!.setValue(d.typeDemande === 'ENTRETIEN_PREVENTIF');
        this.demandes.update((liste) => (liste.some((x) => x.id === d.id) ? liste : [d, ...liste]));

        if (d.ascenseurId != null) {
          this.chargerTechniciensParc(d.ascenseurId);
          return;
        }

        this.siteService.listerParClient(d.clientId).subscribe({
          next: (sites) => {
            if (sites.length > 0) {
              this.sitesClientExistants.set(sites);
              this.necessiteChoixSite.set(true);
            } else {
              this.necessiteCreationSite.set(true);
            }
          },
          error: () => {
            this.necessiteCreationSite.set(true);
          },
        });
      },
      error: () => {
        this.erreurListes.set(`Impossible de charger la demande #${id}.`);
      },
    });
  }

  choisirSiteExistant(site: SiteDTO): void {
    this.necessiteChoixSite.set(false);
    if (site.parcId != null) {
      this.parcSelectionneId.set(site.parcId);
      this.siteSelectionneId.set(site.id);
      this.chargerTechniciensParcId(site.parcId);
    } else {
      this.erreurTechniciens.set("Ce site n'est rattaché à aucun parc.");
    }
  }

  ouvrirCreationNouveauSite(): void {
    this.necessiteChoixSite.set(false);
    this.necessiteCreationSite.set(true);
  }

  surSiteCree(nouveauSite?: any): void {
    this.necessiteCreationSite.set(false);
    this.chargerListes();

    if (nouveauSite?.parcId) {
      this.parcSelectionneId.set(nouveauSite.parcId);
      this.siteSelectionneId.set(nouveauSite.id ?? null);
      this.chargerTechniciensParcId(nouveauSite.parcId);
    }
  }

  private chargerListes(): void {
    this.chargementListes.set(true);
    this.erreurListes.set(null);
    let reste = 2;
    const fini = () => {
      reste--;
      if (reste === 0) this.chargementListes.set(false);
    };

    this.maintenanceService.demandesEnAttente().subscribe({
      next: (data) => {
        this.demandes.set(data);
        fini();
      },
      error: () => {
        this.erreurListes.set('Impossible de charger les demandes en attente.');
        fini();
      },
    });

    this.ascenseurService.listerTous().subscribe({
      next: (res) => {
        this.ascenseurs.set(res.data);
        fini();
      },
      error: () => {
        this.erreurListes.set('Impossible de charger les ascenseurs.');
        fini();
      },
    });
  }

  private chargerTechniciensParc(ascenseurId: number): void {
    this.form.get('technicienResponsableId')!.setValue(null, { emitEvent: false });
    this.responsableSelectionne.set(null);
    this.renfortIds.set([]);
    this.erreurTechniciens.set(null);

    const ascenseur = this.ascenseurs().find((a) => a.id === ascenseurId);
    if (ascenseur?.parcId != null) {
      this.chargerTechniciensParcId(ascenseur.parcId);
      return;
    }
    this.ascenseurService.getById(ascenseurId).subscribe({
      next: (res) => {
        if (res.data.parcId != null) this.chargerTechniciensParcId(res.data.parcId);
      },
      error: () => this.erreurTechniciens.set('Impossible de charger les techniciens du parc.'),
    });
  }

  private chargerTechniciensParcId(parcId: number): void {
    this.technicienService.listerParParc(parcId).subscribe({
      next: (data) => {
        this.techniciensParc.set(data);
        this.erreurTechniciens.set(null);
      },
      error: () => this.erreurTechniciens.set('Impossible de charger les techniciens du parc.'),
    });
  }

  changerOrigine(nouvelleOrigine: 'demande' | 'ascenseur'): void {
    if (this.demandePrechargee()) return;
    this.origine.set(nouvelleOrigine);
    this.iaGeneree.set(false);
    this.iaErreur.set(null);
    const controlDemande = this.form.get('demandeMaintenanceId')!;
    const controlAscenseur = this.form.get('ascenseurId')!;
    if (nouvelleOrigine === 'demande') {
      this.passerEnModeDemande();
    } else {
      controlDemande.setValue(null);
      controlDemande.clearValidators();
      controlAscenseur.setValidators([Validators.required]);
      this.form.get('priorite')!.setValidators([Validators.required]);
    }
    controlDemande.updateValueAndValidity();
    controlAscenseur.updateValueAndValidity();
    this.form.get('priorite')!.updateValueAndValidity();
  }

  private passerEnModeDemande(): void {
    const controlDemande = this.form.get('demandeMaintenanceId')!;
    const controlAscenseur = this.form.get('ascenseurId')!;
    controlAscenseur.setValue(null);
    controlAscenseur.clearValidators();
    controlDemande.setValidators([Validators.required]);
    this.form.get('priorite')!.clearValidators();
  }

  toggleRenfort(id: number, checked: boolean): void {
    this.renfortIds.update((ids) => (checked ? [...ids, id] : ids.filter((x) => x !== id)));
  }

  estRenfort(id: number): boolean {
    return this.renfortIds().includes(id);
  }

  genererDescriptionIa(): void {
    const demandeId = this.demandeSelectionnee;
    if (!demandeId) return;
    this.iaEnCours.set(true);
    this.iaErreur.set(null);
    this.iaGeneree.set(false);
    this.maintenanceService.genererDescriptionIa(demandeId).subscribe({
      next: (texte) => {
        this.iaEnCours.set(false);
        this.iaGeneree.set(true);
        this.form.get('description')!.setValue(texte);
      },
      error: (err) => {
        this.iaEnCours.set(false);
        this.iaErreur.set(err?.error?.message ?? 'La description IA a échoué.');
      },
    });
  }

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.envoiEnCours.set(true);
    this.erreur.set(null);

    const raw = this.form.getRawValue();
    const date =
      raw.dateInterventionPrevue.length === 16
        ? `${raw.dateInterventionPrevue}:00`
        : raw.dateInterventionPrevue;

    const dto: BonTravailCreateDTO = {
      technicienResponsableId: Number(raw.technicienResponsableId),
      technicienIdsRenfort: this.renfortIds(),
      dateInterventionPrevue: date,
      dureeEstimeeMinutes: Number(raw.dureeEstimeeMinutes),
      description: raw.description?.trim() ? raw.description.trim() : null,
      visitePreventive: raw.visitePreventive,
      parcId: this.parcSelectionneId(),
      siteId: this.siteSelectionneId(),
    };

    if (this.origine() === 'demande') {
      dto.demandeMaintenanceId = Number(raw.demandeMaintenanceId);
      dto.ascenseurId = null;
      dto.priorite = null;
    } else {
      dto.ascenseurId = Number(raw.ascenseurId);
      dto.demandeMaintenanceId = null;
      dto.priorite = raw.priorite;
      dto.parcId = null;
      dto.siteId = null;
    }

    this.bonTravailService.creer(dto).subscribe({
      next: () => {
        this.envoiEnCours.set(false);
        this.router.navigate(['/responsable/bons-travail']);
      },
      error: (err) => {
        this.envoiEnCours.set(false);
        this.erreur.set(err?.error?.message ?? 'La création du bon de travail a échoué.');
      },
    });
  }

  prioriteLabel(priorite: string): string {
    const labels: Record<string, string> = {
      BASSE: 'Basse',
      NORMALE: 'Normale',
      URGENTE: 'Urgente',
    };
    return labels[priorite] ?? priorite;
  }
}
