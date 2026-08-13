import { CommonModule } from '@angular/common';
import { Component, inject, signal, computed, effect, untracked } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth';
import { ProfilDTO } from '../../../core/models/utilisateur.model';
import { TYPE_UTILISATEUR_LABELS, TypeUtilisateur } from '../../../core/models/utilisateur.model';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profil.html',
  styleUrl: './profil.scss',
})
export class ProfilComponent {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  profil = signal<ProfilDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  messageSucces = signal<string | null>(null);

  modeEdition = signal(false);
  enregistrementEnCours = signal(false);

  uploadEnCours = signal(false);
  photoErreur = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    telephone: [''],
    nomEntreprise: [''],
    adresse: [''],
  });

  initiales = computed(() => {
    const p = this.profil();
    if (!p) return '?';
    return `${p.prenom?.[0] ?? ''}${p.nom?.[0] ?? ''}`.toUpperCase();
  });

  roleLabel = computed(() => {
    const role = this.profil()?.role as TypeUtilisateur | undefined;
    return (role && TYPE_UTILISATEUR_LABELS[role]) || this.profil()?.role || '';
  });

  estClient = computed(() => this.profil()?.role === 'CLIENT');

  dateMembre = computed(() => {
    const createdAt = this.profil()?.createdAt;
    if (!createdAt) return '';
    return new Date(createdAt).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  });

  constructor() {
    effect(
      () => {
        if (!this.authService.authReady()) return;
        untracked(() => this.charger());
      },
      { allowSignalWrites: true },
    );
  }

  private charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.authService.monProfil().subscribe({
      next: (profil) => {
        this.profil.set(profil);
        this.initialiserFormulaire(profil);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger votre profil.');
        this.chargement.set(false);
      },
    });
  }

  private initialiserFormulaire(profil: ProfilDTO): void {
    this.form.patchValue({
      nom: profil.nom,
      prenom: profil.prenom,
      telephone: profil.telephone ?? '',
      nomEntreprise: profil.nomEntreprise ?? '',
      adresse: profil.adresse ?? '',
    });
  }

  activerEdition(): void {
    this.modeEdition.set(true);
    this.erreur.set(null);
    this.messageSucces.set(null);
  }

  annulerEdition(): void {
    this.modeEdition.set(false);
    const p = this.profil();
    if (p) this.initialiserFormulaire(p);
  }

  enregistrer(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    this.enregistrementEnCours.set(true);
    this.erreur.set(null);
    this.messageSucces.set(null);

    this.authService
      .mettreAJourProfil({
        nom: raw.nom,
        prenom: raw.prenom,
        telephone: raw.telephone || undefined,
        nomEntreprise: raw.nomEntreprise || undefined,
        adresse: raw.adresse || undefined,
      })
      .subscribe({
        next: (profil) => {
          this.profil.set(profil);
          this.modeEdition.set(false);
          this.enregistrementEnCours.set(false);
          this.messageSucces.set('Profil mis à jour avec succès.');
        },
        error: (err) => {
          this.enregistrementEnCours.set(false);
          this.erreur.set(err?.error?.message || 'Échec de la mise à jour du profil.');
        },
      });
  }

  onPhotoChoisie(event: Event): void {
    const input = event.target as HTMLInputElement;
    const fichier = input.files?.[0];
    if (!fichier) return;

    this.uploadEnCours.set(true);
    this.photoErreur.set(null);
    this.messageSucces.set(null);

    this.authService.televerserPhotoProfil(fichier).subscribe({
      next: (profil) => {
        this.profil.set(profil);
        this.uploadEnCours.set(false);
        this.messageSucces.set('Photo de profil mise à jour.');
      },
      error: (err) => {
        this.uploadEnCours.set(false);
        this.photoErreur.set(err?.error?.message || "Échec de l'upload de la photo.");
      },
    });
    input.value = '';
  }
}
