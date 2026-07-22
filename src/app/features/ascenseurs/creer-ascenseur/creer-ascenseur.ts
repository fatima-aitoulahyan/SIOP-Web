import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AscenseurService } from '../services/ascenseur.service';
import { UtilisateurService } from '../../utilisateurs/services/utilisateur';
import { SiteService } from '../../sites/services/site';

import { TypeAscenseur } from '../../../core/models/ascenseur.model';
import { UtilisateurResponseDTO, libelleClient } from '../../../core/models/utilisateur.model';
import { SiteDTO } from '../../../core/models/site.model';
import { Ville, VILLE_LABELS, VILLES } from '../../../core/models/ville.model';

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
  private router = inject(Router);

  submitting = signal(false);
  erreur = signal<string | null>(null);

  types = Object.values(TypeAscenseur);
  readonly libelleClient = libelleClient;

  clients: UtilisateurResponseDTO[] = [];
  sites: SiteDTO[] = [];
  readonly villes = VILLES;
  readonly villeLabels = VILLE_LABELS;

  // ---- Modale "Nouveau site" ----
  showSiteModal = signal(false);
  creatingSite = signal(false);
  siteModalError = signal<string | null>(null);

  siteForm = this.fb.nonNullable.group({
    ville: [null as Ville | null, Validators.required],
    adresse: ['', Validators.required],
    codePostal: [''],
  });

  form = this.fb.nonNullable.group({
    clientId: [null as number | null, Validators.required],
    siteId: [null as number | null, Validators.required],
    nom: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    fabricant: ['', Validators.required],
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

    this.form.controls.clientId.valueChanges.subscribe((clientId) => {
      if (clientId) {
        this.chargerSites(clientId);
      } else {
        this.sites = [];
        this.form.patchValue({ siteId: null });
      }
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
        this.form.patchValue({ siteId: null });
      },
      error: (err) => {
        console.error('Erreur lors du chargement des sites', err);
        this.sites = [];
      },
    });
  }

  // ---- Gestion de la modale "Nouveau site" ----

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
        ville: raw.ville!,
        adresse: raw.adresse,
        codePostal: raw.codePostal || undefined,
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

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.erreur.set(null);

    const raw = this.form.getRawValue();

    this.ascenseurService
      .creer({
        clientId: raw.clientId!,
        siteId: raw.siteId!,
        nom: raw.nom,
        description: raw.description || undefined,
        fabricant: raw.fabricant,
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
          this.erreur.set(err?.error?.message ?? 'Erreur lors de la création.');
        },
      });
  }
}
