import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { VilleDTO } from '../../../core/models/site.model';
import { UtilisateurResponseDTO } from '../../../core/models/utilisateur.model';
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
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private readonly villesUrl = '/api/referentiel/villes';

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
          });
        },
        error: () => this.erreur.set('Impossible de charger le site demandé.'),
      });
    }
  }

  private chargerVilles(): void {
    this.http.get<VilleDTO[]>(this.villesUrl).subscribe({
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
