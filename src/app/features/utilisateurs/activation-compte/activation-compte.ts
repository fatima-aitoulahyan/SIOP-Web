import { Component, inject, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';

@Component({
  selector: 'app-activation-compte',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './activation-compte.html',
  styleUrl: './activation-compte.scss',
})
export class ActivationCompte implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  showPassword = signal(false);
  token = signal<string | null>(null);

  activationForm = this.fb.group({
    motDePasse: ['', [Validators.required, Validators.minLength(8)]],
    confirmMotDePasse: ['', Validators.required],
  });

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.errorMessage.set("Lien d'activation invalide.");
      return;
    }
    this.token.set(token);
  }

  togglePassword() {
    this.showPassword.update((v) => !v);
  }

  onSubmit() {
    if (this.activationForm.invalid || !this.token()) {
      this.activationForm.markAllAsTouched();
      return;
    }

    const { motDePasse, confirmMotDePasse } = this.activationForm.getRawValue();
    if (motDePasse !== confirmMotDePasse) {
      this.errorMessage.set('Les mots de passe ne correspondent pas.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.authService.activerCompte({ token: this.token()!, motDePasse: motDePasse! }).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMessage.set('Compte activé avec succès ! Redirection...');
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err?.error?.message || "Erreur lors de l'activation.");
      },
    });
  }
}
