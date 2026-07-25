import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { VilleDTO } from '../../../core/models/site.model';
import { UtilisateurResponseDTO } from '../../../core/models/utilisateur.model';
import { SiteService } from '../services/site';
import { VilleService } from '../services/ville.service';
import { UtilisateurService } from '../../utilisateurs/services/utilisateur';

@Component({
  selector: 'app-site-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './site-form.html',
  styleUrls: ['./site-form.scss'],
})
export class SiteFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private siteService = inject(SiteService);
  private villeService = inject(VilleService);
  private utilisateurService = inject(UtilisateurService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  modeEdition = signal(false);
  siteId = signal<number | null>(null);
  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);
  villes = signal<VilleDTO[]>([]);

  clients: UtilisateurResponseDTO[] = [];

  form = this.fb.nonNullable.group({
    clientId: [null as number | null, Validators.required],
    villeId: [null as number | null, Validators.required],
    adresse: ['', Validators.required],
    codePostal: [''],
    zone: [''],
  });

  // ---- Modale "Nouvelle ville" ----
  showVilleModal = signal(false);
  creatingVille = signal(false);
  villeModalError = signal<string | null>(null);

  villeForm = this.fb.nonNullable.group({
    nom: ['', Validators.required],
  });

  ngOnInit(): void {
    this.chargerVilles();
    this.chargerClients();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.modeEdition.set(true);
      this.siteId.set(id);
      this.form.controls.clientId.disable();

      this.siteService.getById(id).subscribe({
        next: (site) => {
          this.form.patchValue({
            clientId: site.clientId,
            adresse: site.adresse,
            codePostal: site.codePostal ?? '',
            zone: site.zone ?? '',
          });
        },
        error: () => this.erreur.set('Impossible de charger le site demandé.'),
      });
    }
  }

  private chargerVilles(): void {
    this.villeService.listerTous().subscribe({
      next: (villes) => this.villes.set(villes),
      error: () => this.erreur.set('Impossible de charger la liste des villes.'),
    });
  }

  chargerClients(): void {
    this.utilisateurService.getClients().subscribe({
      next: (clients) => {
        this.clients = clients;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des clients', err);
        this.erreur.set('Impossible de charger la liste des clients.');
      },
    });
  }


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
        this.villes.set([...this.villes(), nouvelleVille]);
        this.form.patchValue({ villeId: nouvelleVille.id });
        this.showVilleModal.set(false);
      },
      error: (err) => {
        this.creatingVille.set(false);
        this.villeModalError.set(err?.error?.message ?? 'Erreur lors de la création de la ville.');
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
    const valeurs = this.form.getRawValue();

    if (this.modeEdition() && this.siteId() !== null) {
      this.siteService
        .modifier(this.siteId()!, {
          villeId: valeurs.villeId ?? undefined,
          adresse: valeurs.adresse,
          codePostal: valeurs.codePostal || null,
          zone: valeurs.zone || undefined,
        })
        .subscribe({
          next: () => this.router.navigate(['/sites']),
          error: () => {
            this.erreur.set('La mise à jour du site a échoué.');
            this.envoiEnCours.set(false);
          },
        });
    } else {
      this.siteService
        .creer({
          clientId: valeurs.clientId!,
          villeId: valeurs.villeId!,
          adresse: valeurs.adresse,
          codePostal: valeurs.codePostal || null,
          zone: valeurs.zone || undefined,
        })
        .subscribe({
          next: () => this.router.navigate(['/sites']),
          error: () => {
            this.erreur.set('La création du site a échoué.');
            this.envoiEnCours.set(false);
          },
        });
    }
  }
}
