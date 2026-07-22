import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UtilisateurService } from '../services/utilisateur';
import { AuthService } from '../../../core/auth/auth';
import { TypeUtilisateur, TYPE_UTILISATEUR_LABELS } from '../../../core/models/utilisateur.model';

@Component({
  selector: 'app-utilisateur-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './utilisateur-form.html',
  styleUrl: './utilisateur-form.scss',
})
export class UtilisateurForm {
  private fb = inject(FormBuilder);
  private utilisateurService = inject(UtilisateurService);
  private router = inject(Router);
  private authService = inject(AuthService);

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  showPassword = signal(false);

  currentRole = this.authService.currentRole;
  typesUtilisateur = Object.values(TypeUtilisateur);
  labels = TYPE_UTILISATEUR_LABELS;

  utilisateurForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    telephone: [''],
    motDePasse: ['', [Validators.required, Validators.minLength(6)]],
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    nomEntreprise: [''],
    type: [null as TypeUtilisateur | null, Validators.required],
    adresse: [''],
    specialite: [''],
  });

  togglePassword() {
    this.showPassword.update((v) => !v);
  }

  onSubmit() {
    if (this.utilisateurForm.invalid) {
      this.utilisateurForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const dto = this.utilisateurForm.getRawValue();

    this.utilisateurService.creer(dto as any).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.successMessage.set(`Utilisateur ${res.prenom} ${res.nom} créé avec succès.`);
        this.utilisateurForm.reset();
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err?.error?.message || "Erreur lors de la création de l'utilisateur.",
        );
      },
    });
  }

  allerVersListe() {
    this.router.navigate(['/utilisateurs']);
  }
}
