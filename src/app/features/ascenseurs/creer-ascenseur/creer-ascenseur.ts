import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AscenseurService } from '../services/ascenseur.service';
import { UtilisateurService } from '../../utilisateurs/services/utilisateur';
import { SiteService } from '../../sites/services/site';
import { VilleService } from '../../sites/services/ville.service';

import { TypeAscenseur } from '../../../core/models/ascenseur.model';
import { UtilisateurResponseDTO } from '../../../core/models/utilisateur.model';
import { SiteDTO } from '../../../core/models/site.model';
import { VilleDTO } from '../../../core/models/ville.model';
import { ParcDTO } from '../../../core/models/parc.model';
import { ParcService } from '../../parcs/services/parc.service';

@Component({
  selector: 'app-creer-ascenseur',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './creer-ascenseur.html',
  styleUrl: './creer-ascenseur.scss',
})
export class CreerAscenseur implements OnInit {
  private fb = inject(FormBuilder);
  private ascenseurService = inject(AscenseurService);
  private utilisateurService = inject(UtilisateurService);
  private siteService = inject(SiteService);
  private villeService = inject(VilleService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  submitting = signal(false);
  erreur = signal<string | null>(null);

  // ---- Mode édition ----
  ascenseurId = signal<number | null>(null);
  isEditMode = signal(false);
  chargementInitial = signal(false);

  types = Object.values(TypeAscenseur);

  clients: UtilisateurResponseDTO[] = [];
  sites: SiteDTO[] = [];
  villes: VilleDTO[] = [];
  parcs: ParcDTO[] = [];

  // ---- Modale "Nouveau site" ----
  showSiteModal = signal(false);
  creatingSite = signal(false);
  siteModalError = signal<string | null>(null);

   siteForm = this.fb.nonNullable.group({
   villeId: [null as number | null, Validators.required],
   parcId: [null as number | null, Validators.required],
   adresse: ['', Validators.required],
  });

  // ---- Modale "Nouvelle ville" ----
  showVilleModal = signal(false);
  creatingVille = signal(false);
  villeModalError = signal<string | null>(null);

  villeForm = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    region: [''],
    codePostal: [''],
  });

  form = this.fb.nonNullable.group({
    clientId: [null as number | null, Validators.required],
    siteId: [null as number | null, Validators.required],
    nom: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    fabricant: ['', Validators.required],
    marque: ['', Validators.required],
    modele: [''],
    numeroSerie: [''],
    codeBarre: [''],
    puissance: [''],
    nombreEtages: [null as number | null, Validators.min(1)],
    capacitePersonnes: [null as number | null, Validators.min(1)],
    chargeMaxKg: [null as number | null, Validators.min(1)],
    vitesse: [null as number | null, Validators.min(0)],
    type: [null as TypeAscenseur | null],
    dateMiseEnService: [''],
    dateExpirationGarantie: [''],
    informationsSupplementaires: [''],
  });

  ngOnInit(): void {
    this.chargerClients();
    this.chargerVilles();
    this.chargerParcs(); 

    this.form.controls.clientId.valueChanges.subscribe((clientId) => {
      if (clientId) {
        this.chargerSites(clientId);
      } else {
        this.sites = [];
        this.form.patchValue({ siteId: null });
      }
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode.set(true);
      this.ascenseurId.set(+idParam);
      this.chargerAscenseurExistant(+idParam);
    }
  }

  chargerAscenseurExistant(id: number): void {
    this.chargementInitial.set(true);

    this.ascenseurService.getById(id).subscribe({
      next: (res) => {
        const a = res.data!;

        // Charge d'abord les sites du client, puis remplit le formulaire
        this.siteService.listerParClient(a.clientId).subscribe({
          next: (sites) => {
            this.sites = sites;

            this.form.patchValue({
              clientId: a.clientId,
              siteId: a.siteId,
              nom: a.nom,
              description: a.description ?? '',
              fabricant: a.fabricant,
              marque: a.marque,
              modele: a.modele ?? '',
              numeroSerie: a.numeroSerie ?? '',
              codeBarre: a.codeBarre ?? '',
              puissance: a.puissance ?? '',
              nombreEtages: a.nombreEtages ?? null,
              capacitePersonnes: a.capacitePersonnes ?? null,
              chargeMaxKg: a.chargeMaxKg ?? null,
              vitesse: a.vitesse ?? null,
              type: a.type ?? null,
              dateMiseEnService: a.dateMiseEnService ?? '',
              dateExpirationGarantie: a.dateExpirationGarantie ?? '',
              informationsSupplementaires: a.informationsSupplementaires ?? '',
            });

            this.chargementInitial.set(false);
          },
          error: () => {
            this.chargementInitial.set(false);
          },
        });
      },
      error: (err) => {
        this.chargementInitial.set(false);
        this.erreur.set(err?.error?.message ?? "Erreur lors du chargement de l'ascenseur.");
      },
    });
  }

  chargerClients(): void {
    this.utilisateurService.getClients().subscribe({
      next: (clients) => (this.clients = clients),
      error: (err) => console.error('Erreur lors du chargement des clients', err),
    });
  }

  chargerSites(clientId: number): void {
    this.siteService.listerParClient(clientId).subscribe({
      next: (sites) => {
        this.sites = sites;
        // En mode création on réinitialise le site choisi ; pas en mode édition initial
        if (!this.isEditMode()) {
          this.form.patchValue({ siteId: null });
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des sites', err);
        this.sites = [];
      },
    });
  }

  chargerVilles(): void {
    this.villeService.listerTous().subscribe({
      next: (villes) => (this.villes = villes),
      error: (err) => console.error('Erreur lors du chargement des villes', err),
    });
  }
  private parcService = inject(ParcService);

  chargerParcs(): void {
  this.parcService.listerTous().subscribe({
    next: (parcs) => (this.parcs = parcs),
    error: (err) => console.error('Erreur lors du chargement des parcs', err),
  });
}

  ouvrirModalSite(): void {
    const clientId = this.form.controls.clientId.value;
    if (!clientId) {
      this.form.controls.clientId.markAsTouched();
      return;
    }
    this.siteModalError.set(null);
    this.siteForm.reset();
    this.showSiteModal.set(true);
  }

  fermerModalSite(): void {
    this.showSiteModal.set(false);
  }

  onSubmitSite(): void {
    if (this.siteForm.invalid) {
      this.siteForm.markAllAsTouched();
      return;
    }

    const clientId = this.form.controls.clientId.value;
    if (!clientId) return;

    this.creatingSite.set(true);
    this.siteModalError.set(null);

    const raw = this.siteForm.getRawValue();

    this.siteService
      .creer({
       clientId,
       villeId: raw.villeId!,
       parcId: raw.parcId!,
       adresse: raw.adresse,
     })
      .subscribe({
        next: (nouveauSite) => {
          this.creatingSite.set(false);
          this.sites = [...this.sites, nouveauSite];
          this.form.patchValue({ siteId: nouveauSite.id });
          this.showSiteModal.set(false);
        },
        error: (err) => {
          this.creatingSite.set(false);
          this.siteModalError.set(err?.error?.message ?? 'Erreur lors de la création du site.');
        },
      });
  }

  // ---- Gestion de la modale "Nouvelle ville" ----

  ouvrirModalVille(): void {
    this.villeModalError.set(null);
    this.villeForm.reset();
    this.showVilleModal.set(true);
  }

  fermerModalVille(): void {
    this.showVilleModal.set(false);
  }

  onSubmitVille(): void {
    if (this.villeForm.invalid) {
      this.villeForm.markAllAsTouched();
      return;
    }

    this.creatingVille.set(true);
    this.villeModalError.set(null);

    this.villeService.creer(this.villeForm.getRawValue()).subscribe({
      next: (nouvelleVille) => {
        this.creatingVille.set(false);
        this.villes = [...this.villes, nouvelleVille];
        this.siteForm.patchValue({ villeId: nouvelleVille.id });
        this.showVilleModal.set(false);
      },
      error: (err) => {
        this.creatingVille.set(false);
        this.villeModalError.set(err?.error?.message ?? 'Erreur lors de la création de la ville.');
      },
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.erreur.set(null);

    const raw = this.form.getRawValue();

    if (this.isEditMode()) {
      this.ascenseurService
        .modifier(this.ascenseurId()!, {
          nom: raw.nom,
          siteId: raw.siteId!,
          description: raw.description || undefined,
          fabricant: raw.fabricant,
          marque: raw.marque,
          modele: raw.modele || undefined,
          numeroSerie: raw.numeroSerie || undefined,
          codeBarre: raw.codeBarre || undefined,
          puissance: raw.puissance || undefined,
          nombreEtages: raw.nombreEtages ?? undefined,
          capacitePersonnes: raw.capacitePersonnes ?? undefined,
          chargeMaxKg: raw.chargeMaxKg ?? undefined,
          vitesse: raw.vitesse ?? undefined,
          type: raw.type ?? undefined,
          dateMiseEnService: raw.dateMiseEnService || undefined,
          dateExpirationGarantie: raw.dateExpirationGarantie || undefined,
          informationsSupplementaires: raw.informationsSupplementaires || undefined,
        })
        .subscribe({
          next: (res) => {
            this.submitting.set(false);
            this.router.navigate(['/ascenseurs', res.data!.id]);
          },
          error: (err) => {
            this.submitting.set(false);
            this.erreur.set(err?.error?.message ?? 'Erreur lors de la modification.');
          },
        });
    } else {
      this.ascenseurService
        .creer({
          clientId: raw.clientId!,
          siteId: raw.siteId!,
          nom: raw.nom,
          description: raw.description || undefined,
          fabricant: raw.fabricant,
          marque: raw.marque,
          modele: raw.modele || undefined,
          numeroSerie: raw.numeroSerie || undefined,
          codeBarre: raw.codeBarre || undefined,
          puissance: raw.puissance || undefined,
          nombreEtages: raw.nombreEtages ?? undefined,
          capacitePersonnes: raw.capacitePersonnes ?? undefined,
          chargeMaxKg: raw.chargeMaxKg ?? undefined,
          vitesse: raw.vitesse ?? undefined,
          type: raw.type ?? undefined,
          dateMiseEnService: raw.dateMiseEnService || undefined,
          dateExpirationGarantie: raw.dateExpirationGarantie || undefined,
          informationsSupplementaires: raw.informationsSupplementaires || undefined,
        })
        .subscribe({
          next: (res) => {
            this.submitting.set(false);
            this.router.navigate(['/fiches-ascenseur', res.data!.id]);
          },
          error: (err) => {
            this.submitting.set(false);
            this.erreur.set(err?.error?.message ?? 'Erreur lors de la création.');
          },
        });
    }
  }
}
