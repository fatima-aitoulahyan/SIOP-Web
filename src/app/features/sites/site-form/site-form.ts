import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Ville, VILLE_LABELS, VILLES } from '../../../core/models/ville.model';
import { UtilisateurResponseDTO, libelleClient } from '../../../core/models/utilisateur.model';
import { SiteService } from '../services/site';
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
  private utilisateurService = inject(UtilisateurService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  modeEdition = signal(false);
  siteId = signal<number | null>(null);
  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);

  readonly villes = VILLES;
  readonly villeLabels = VILLE_LABELS;

  clients: UtilisateurResponseDTO[] = [];
  readonly libelleClient = libelleClient;

  form = this.fb.nonNullable.group({
    clientId: [null as number | null, Validators.required],
    ville: [null as Ville | null, Validators.required],
    adresse: ['', Validators.required],
    codePostal: [''],
  });

  ngOnInit(): void {
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
            ville: site.ville,
            adresse: site.adresse,
            codePostal: site.codePostal ?? '',
          });
        },
        error: () => this.erreur.set('Impossible de charger le site demandé.'),
      });
    }
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
          ville: valeurs.ville ?? undefined,
          adresse: valeurs.adresse,
          codePostal: valeurs.codePostal || null,
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
          ville: valeurs.ville!,
          adresse: valeurs.adresse,
          codePostal: valeurs.codePostal || null,
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
