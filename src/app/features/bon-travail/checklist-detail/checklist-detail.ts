import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/auth/auth';
import { BonTravailService } from '../services/bon-travail.service';
import { environment } from '../../../../environments/environment';
import {
  BonTravailDTO,
  ChecklistMaintenanceDTO,
  ClotureBonTravailDTO,
  GraviteAnomalie,
  ItemCheckListDTO,
  StatutBonTravail,
  StatutItem,
} from '../../../core/models/bon-travail.model';

interface ItemEdit {
  statut: StatutItem;
  gravite: GraviteAnomalie | null;
  remarque: string;
}

@Component({
  selector: 'app-checklist-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, FormsModule],
  templateUrl: './checklist-detail.html',
  styleUrls: ['./checklist-detail.scss'],
})
export class ChecklistDetailComponent implements OnInit {
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private bonTravailService = inject(BonTravailService);

  bonTravail = signal<BonTravailDTO | null>(null);
  checklist = signal<ChecklistMaintenanceDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  pasDeChecklist = signal(false);

  demarrageEnCours = signal(false);
  sauvegardeItem = signal<number | null>(null);
  itemErreur = signal<string | null>(null);

  photoEnCours = signal<number | null>(null);
  photoErreur = signal<string | null>(null);

  modaleCloture = signal(false);
  clotureEnCours = signal(false);
  clotureErreur = signal<string | null>(null);

  demarrageBtEnCours = signal(false);
  modaleClotureBt = signal(false);
  clotureBtEnCours = signal(false);
  clotureBtErreur = signal<string | null>(null);

  edits = signal<Record<number, ItemEdit>>({});

  tempsEcoule = signal(0);
  debutLocalMs: number | null = null;

  statutItemOptions = Object.values(StatutItem);
  graviteOptions = Object.values(GraviteAnomalie);

  clotureForm = this.fb.nonNullable.group({
    bilanIntervention: ['', [Validators.required]],
    estMaintenance: [false],
    estDepannage: [false],
    estTravaux: [false],
  });

  clotureBtForm = this.fb.nonNullable.group({
    diagnostic: ['', [Validators.required]],
    causeIdentifiee: [''],
    actionRealisee: ['', [Validators.required]],
    piecesRemplacees: [''],
    essaiConcluant: [null as boolean | null],
    recommandations: [''],
  });

  estTechnicien = computed(() => this.authService.currentRole() === 'TECHNICIEN');

  routeRetour = computed(() =>
    this.estTechnicien() ? '/technicien/bons-travail' : '/responsable/bons-travail',
  );

  estLectureSeule = computed(() => {
    const bt = this.bonTravail();
    if (!this.estTechnicien()) return true;
    return bt?.statut === StatutBonTravail.TERMINE || bt?.statut === StatutBonTravail.ANNULE;
  });

  peutDemarrer = computed(() => {
    const cl = this.checklist();
    if (!cl || !this.estTechnicien() || this.estLectureSeule()) return false;
    return cl.heureArrivee == null;
  });

  interventionDemarree = computed(() => {
    const cl = this.checklist();
    return !!cl && cl.heureArrivee != null;
  });

  interventionEnCours = computed(() => {
    const cl = this.checklist();
    if (cl) return cl.heureArrivee != null && cl.heureDepart == null;
    const bt = this.bonTravail();
    return this.pasDeChecklist() && bt?.statut === StatutBonTravail.EN_COURS;
  });

  peutDemarrerBt = computed(() => {
    const bt = this.bonTravail();
    return !!bt && this.pasDeChecklist() && this.estTechnicien() && bt.statut === StatutBonTravail.PLANIFIE;
  });

  btEnCours = computed(() => {
    const bt = this.bonTravail();
    return !!bt && this.pasDeChecklist() && this.estTechnicien() && bt.statut === StatutBonTravail.EN_COURS;
  });

  dureeTerminee = computed(() => {
    const cl = this.checklist();
    if (!cl?.heureArrivee || !cl.heureDepart) return null;
    return ChecklistDetailComponent.diffHeures(cl.heureArrivee, cl.heureDepart);
  });

  dureeBtTerminee = computed(() => {
    const bt = this.bonTravail();
    if (!this.pasDeChecklist() || !bt?.dateDebutReelle || !bt.dateFinReelle) return null;
    const diff = Math.max(0, Date.parse(bt.dateFinReelle) - Date.parse(bt.dateDebutReelle));
    return ChecklistDetailComponent.formaterDuree(diff);
  });

  tempsFormate = computed(() => ChecklistDetailComponent.formaterDuree(this.tempsEcoule()));

  constructor() {
    effect(() => {
      const cl = this.checklist();
      const bt = this.bonTravail();
      let debut: number | null = null;
      let terminee = false;

      if (cl && cl.heureArrivee != null) {
        if (cl.heureDepart != null) {
          terminee = true;
        } else {
          debut = this.debutLocalMs ?? ChecklistDetailComponent.debutDepuisHeure(cl.heureArrivee);
        }
      } else if (this.pasDeChecklist() && bt?.dateDebutReelle) {
        if (bt.dateFinReelle) {
          terminee = true;
        } else {
          debut = Date.parse(bt.dateDebutReelle);
        }
      }

      if (debut == null || terminee) {
        this.tempsEcoule.set(0);
        return;
      }
      const maj = () => this.tempsEcoule.set(Math.max(0, Date.now() - debut!));
      maj();
      const id = setInterval(maj, 1000);
      return () => clearInterval(id);
    }, { allowSignalWrites: true });
  }

  private static formaterDuree(ms: number): string {
    const total = Math.floor(ms / 1000);
    const h = String(Math.floor(total / 3600)).padStart(2, '0');
    const m = String(Math.floor((total % 3600) / 60)).padStart(2, '0');
    const s = String(total % 60).padStart(2, '0');
    return `${h}:${m}:${s}`;
  }

  private static debutDepuisHeure(heure: string): number {
    const [h, m, s] = heure.split(':').map((part) => Number(part));
    const maintenant = new Date();
    let debut = new Date(
      maintenant.getFullYear(),
      maintenant.getMonth(),
      maintenant.getDate(),
      h || 0,
      m || 0,
      s || 0,
    );
    if (debut.getTime() > maintenant.getTime()) {
      debut = new Date(debut.getTime() - 24 * 60 * 60 * 1000);
    }
    return debut.getTime();
  }

  private static diffHeures(debut: string, fin: string): string {
    const [h1, m1, s1] = debut.split(':').map((part) => Number(part));
    const [h2, m2, s2] = fin.split(':').map((part) => Number(part));
    let ms1 = ((h1 || 0) * 3600 + (m1 || 0) * 60 + (s1 || 0)) * 1000;
    let ms2 = ((h2 || 0) * 3600 + (m2 || 0) * 60 + (s2 || 0)) * 1000;
    if (ms2 < ms1) ms2 += 24 * 60 * 60 * 1000;
    const total = Math.floor((ms2 - ms1) / 1000);
    const h = String(Math.floor(total / 3600)).padStart(2, '0');
    const m = String(Math.floor((total % 3600) / 60)).padStart(2, '0');
    const s = String(total % 60).padStart(2, '0');
    return `${h}:${m}:${s}`;
  }

  tousVerifies = computed(() => {
    const items = this.checklist()?.items;
    return !!items && items.every((i) => i.statut !== StatutItem.NON_VERIFIE);
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.chargement.set(true);
    const detail$ = this.estTechnicien()
      ? this.bonTravailService.getIntervention(id)
      : this.bonTravailService.getDetail(id);

    detail$.subscribe({
      next: (bt) => {
        this.bonTravail.set(bt);
        this.chargerChecklist(bt.id);
      },
      error: () => {
        this.erreur.set('Impossible de charger le bon de travail.');
        this.chargement.set(false);
      },
    });
  }

  private chargerChecklist(bonTravailId: number): void {
    this.bonTravailService.checklistParBonTravail(bonTravailId).subscribe({
      next: (cl) => {
        this.checklist.set(cl);
        this.initialiserEdits(cl);
        this.chargement.set(false);
      },
      error: (err) => {
        if (err.status === 404) {
          this.pasDeChecklist.set(true);
        } else {
          this.erreur.set('Impossible de charger la checklist.');
        }
        this.chargement.set(false);
      },
    });
  }

  private initialiserEdits(cl: ChecklistMaintenanceDTO): void {
    const rec: Record<number, ItemEdit> = {};
    cl.items.forEach((i) => {
      rec[i.id] = { statut: i.statut, gravite: i.gravite ?? null, remarque: i.remarque ?? '' };
    });
    this.edits.set(rec);
  }

  edit(item: ItemCheckListDTO): ItemEdit {
    return (
      this.edits()[item.id] ?? {
        statut: item.statut,
        gravite: item.gravite ?? null,
        remarque: item.remarque ?? '',
      }
    );
  }

  majItem(
    item: ItemCheckListDTO,
    champ: 'statut' | 'gravite' | 'remarque',
    valeur: StatutItem | GraviteAnomalie | string,
  ): void {
    const current = this.edit(item);
    this.edits.update((rec) => ({
      ...rec,
      [item.id]: { ...current, [champ]: valeur },
    }));
  }

  demarrer(): void {
    const cl = this.checklist();
    if (!cl) return;
    this.demarrageEnCours.set(true);
    this.bonTravailService.demarrerChecklist(cl.id).subscribe({
      next: (reponse) => {
        this.debutLocalMs = Date.now();
        this.checklist.set(reponse);
        this.initialiserEdits(reponse);
        this.demarrageEnCours.set(false);
        this.rafraichirBonTravail();
      },
      error: (err) => {
        this.demarrageEnCours.set(false);
        this.erreur.set(err?.error?.message ?? 'Impossible de démarrer l\'intervention.');
      },
    });
  }

  enregistrerItem(item: ItemCheckListDTO): void {
    const e = this.edit(item);
    if (e.statut === StatutItem.ANOMALIE_DETECTEE && !e.gravite) {
      this.itemErreur.set('La gravité est obligatoire pour une anomalie.');
      return;
    }
    this.itemErreur.set(null);
    this.sauvegardeItem.set(item.id);
    this.bonTravailService
      .cocherItem(item.id, {
        statut: e.statut,
        gravite: e.statut === StatutItem.ANOMALIE_DETECTEE ? e.gravite : null,
        remarque: e.remarque?.trim() ? e.remarque.trim() : null,
      })
      .subscribe({
        next: (reponse) => {
          this.checklist.set(reponse);
          const sauve = reponse.items.find((i) => i.id === item.id);
          if (sauve) {
            this.edits.update((rec) => ({
              ...rec,
              [sauve.id]: {
                statut: sauve.statut,
                gravite: sauve.gravite ?? null,
                remarque: sauve.remarque ?? '',
              },
            }));
          }
          this.sauvegardeItem.set(null);
        },
        error: (err) => {
          this.sauvegardeItem.set(null);
          this.itemErreur.set(err?.error?.message ?? "Erreur lors de l'enregistrement de l'item.");
        },
      });
  }

  ajouterPhoto(item: ItemCheckListDTO, event: Event): void {
    const input = event.target as HTMLInputElement;
    const fichier = input.files?.[0];
    if (!fichier) return;

    this.photoErreur.set(null);
    this.photoEnCours.set(item.id);

    const fd = new FormData();
    fd.append('fichier', fichier);
    fd.append('entiteType', 'ITEM_CHECKLIST');
    fd.append('entiteId', String(item.id));

    this.http.post(`${environment.apiUrl}/pieces-jointes`, fd).subscribe({
      next: () => {
        this.photoEnCours.set(null);
        input.value = '';
        const bt = this.bonTravail();
        if (bt) this.chargerChecklist(bt.id);
      },
      error: () => {
        this.photoEnCours.set(null);
        this.photoErreur.set("Erreur lors du téléversement de la photo.");
      },
    });
  }

  ouvrirCloture(): void {
    if (!this.tousVerifies()) return;
    this.clotureErreur.set(null);
    this.clotureForm.reset({ bilanIntervention: '', estMaintenance: false, estDepannage: false, estTravaux: false });
    this.modaleCloture.set(true);
  }

  fermerCloture(): void {
    this.modaleCloture.set(false);
  }

  cloturer(): void {
    if (this.clotureForm.invalid) {
      this.clotureForm.markAllAsTouched();
      return;
    }
    const cl = this.checklist();
    if (!cl) return;

    const raw = this.clotureForm.getRawValue();
    this.clotureEnCours.set(true);
    this.clotureErreur.set(null);

    this.bonTravailService
      .cloturerChecklist(cl.id, {
        bilanIntervention: raw.bilanIntervention,
        estMaintenance: raw.estMaintenance,
        estDepannage: raw.estDepannage,
        estTravaux: raw.estTravaux,
      })
      .subscribe({
        next: (reponse) => {
          this.checklist.set(reponse);
          this.initialiserEdits(reponse);
          this.clotureEnCours.set(false);
          this.modaleCloture.set(false);
          this.rafraichirBonTravail();
        },
        error: (err) => {
          this.clotureEnCours.set(false);
          this.clotureErreur.set(err?.error?.message ?? 'La clôture a échoué.');
        },
      });
  }

  demarrerBt(): void {
    const bt = this.bonTravail();
    if (!bt) return;
    this.demarrageBtEnCours.set(true);
    this.bonTravailService.demarrerIntervention(bt.id).subscribe({
      next: (reponse) => {
        this.debutLocalMs = Date.now();
        this.bonTravail.set(reponse);
        this.demarrageBtEnCours.set(false);
      },
      error: (err) => {
        this.demarrageBtEnCours.set(false);
        this.erreur.set(err?.error?.message ?? "Impossible de démarrer l'intervention.");
      },
    });
  }

  ouvrirClotureBt(): void {
    this.clotureBtErreur.set(null);
    this.clotureBtForm.reset({
      diagnostic: '',
      causeIdentifiee: '',
      actionRealisee: '',
      piecesRemplacees: '',
      essaiConcluant: null,
      recommandations: '',
    });
    this.modaleClotureBt.set(true);
  }

  fermerClotureBt(): void {
    this.modaleClotureBt.set(false);
  }

  cloturerBt(): void {
    if (this.clotureBtForm.invalid) {
      this.clotureBtForm.markAllAsTouched();
      return;
    }
    const bt = this.bonTravail();
    if (!bt) return;

    const raw = this.clotureBtForm.getRawValue();
    const dto: ClotureBonTravailDTO = {
      diagnostic: raw.diagnostic,
      actionRealisee: raw.actionRealisee,
      causeIdentifiee: raw.causeIdentifiee?.trim() ? raw.causeIdentifiee.trim() : null,
      piecesRemplacees: raw.piecesRemplacees?.trim() ? raw.piecesRemplacees.trim() : null,
      essaiConcluant: raw.essaiConcluant,
      recommandations: raw.recommandations?.trim() ? raw.recommandations.trim() : null,
    };

    this.clotureBtEnCours.set(true);
    this.clotureBtErreur.set(null);
    this.bonTravailService.terminerIntervention(bt.id, dto).subscribe({
      next: (reponse) => {
        this.bonTravail.set(reponse);
        this.clotureBtEnCours.set(false);
        this.modaleClotureBt.set(false);
      },
      error: (err) => {
        this.clotureBtEnCours.set(false);
        this.clotureBtErreur.set(err?.error?.message ?? 'La clôture a échoué.');
      },
    });
  }

  essaiLabel(valeur: boolean | null): string {
    if (valeur == null) return '—';
    return valeur ? 'Oui' : 'Non';
  }

  private rafraichirBonTravail(): void {
    const bt = this.bonTravail();
    if (!bt) return;
    const source$ = this.estTechnicien()
      ? this.bonTravailService.getIntervention(bt.id)
      : this.bonTravailService.getDetail(bt.id);
    source$.subscribe({ next: (d) => this.bonTravail.set(d) });
  }

  ouvrirPhoto(url: string): void {
    window.open(url, '_blank');
  }

  equipeLabel(bt: BonTravailDTO): string {
    return bt.techniciens.map((t) => `${t.prenom} ${t.nom}`).join(', ');
  }

  statutItemLabel(statut: string): string {
    const labels: Record<string, string> = {
      NON_VERIFIE: 'Non vérifié',
      CONFORME: 'Conforme',
      ANOMALIE_DETECTEE: 'Anomalie',
    };
    return labels[statut] ?? statut;
  }

  statutItemClass(statut: string): string {
    const classes: Record<string, string> = {
      NON_VERIFIE: 'item-nonverifie',
      CONFORME: 'item-conforme',
      ANOMALIE_DETECTEE: 'item-anomalie',
    };
    return classes[statut] ?? '';
  }

  graviteLabel(gravite: string): string {
    const labels: Record<string, string> = {
      MINEURE: 'Mineure',
      MAJEURE: 'Majeure',
      CRITIQUE: 'Critique',
    };
    return labels[gravite] ?? gravite;
  }

  graviteClass(gravite: string): string {
    const classes: Record<string, string> = {
      MINEURE: 'gravite-mineure',
      MAJEURE: 'gravite-majeure',
      CRITIQUE: 'gravite-critique',
    };
    return classes[gravite] ?? '';
  }

  statutBtLabel(statut: string): string {
    const labels: Record<string, string> = {
      PLANIFIE: 'Planifié',
      EN_COURS: 'En cours',
      TERMINE: 'Terminé',
      ANNULE: 'Annulé',
    };
    return labels[statut] ?? statut;
  }

  statutBtClass(statut: string): string {
    const classes: Record<string, string> = {
      PLANIFIE: 'statut-bt-planifie',
      EN_COURS: 'statut-bt-cours',
      TERMINE: 'statut-bt-termine',
      ANNULE: 'statut-bt-annule',
    };
    return classes[statut] ?? '';
  }
}