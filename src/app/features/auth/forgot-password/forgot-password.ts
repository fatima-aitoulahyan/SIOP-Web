import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  loading = signal(false);
  emailEnvoye = signal(false);
  errorMessage = signal<string | null>(null);

  forgotPasswordForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  onSubmit(): void {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const email = this.forgotPasswordForm.value.email as string;

    this.authService.motDePasseOublie(email).subscribe({
      next: () => {
        this.loading.set(false);
        this.emailEnvoye.set(true);
      },
      error: () => {
        this.loading.set(false);
        // On affiche quand même le message de succès pour ne pas révéler
        // si l'email existe ou non dans le système.
        this.emailEnvoye.set(true);
      },
    });
  }
}
