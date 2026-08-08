import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';

function motsDePasseIdentiques(control: AbstractControl): ValidationErrors | null {
  const mdp = control.get('nouveauMotDePasse')?.value;
  const confirmation = control.get('confirmationMotDePasse')?.value;
  return mdp === confirmation ? null : { motsDePasseDifferents: true };
}

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
})
export class ResetPasswordComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  loading = signal(false);
  succes = signal(false);
  errorMessage = signal<string | null>(null);
  token = signal<string | null>(null);

  resetPasswordForm = this.fb.group(
    {
      nouveauMotDePasse: ['', [Validators.required, Validators.minLength(8)]],
      confirmationMotDePasse: ['', Validators.required],
    },
    { validators: motsDePasseIdentiques },
  );

  constructor() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.errorMessage.set('Lien de réinitialisation invalide.');
    }
    this.token.set(token);
  }

  onSubmit(): void {
    if (this.resetPasswordForm.invalid || !this.token()) {
      this.resetPasswordForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const nouveauMotDePasse = this.resetPasswordForm.value.nouveauMotDePasse as string;

    this.authService.reinitialiserMotDePasse(this.token()!, nouveauMotDePasse).subscribe({
      next: () => {
        this.loading.set(false);
        this.succes.set(true);
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err?.error?.message ?? 'Ce lien a expiré ou est invalide. Veuillez refaire une demande.',
        );
      },
    });
  }
}
