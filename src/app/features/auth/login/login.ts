import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  errorMessage = signal<string | null>(null);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', Validators.required],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.authService.login(this.loginForm.value as any).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.redirigerSelonRole(res.data.type);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Email ou mot de passe incorrect.');
      },
    });
  }

private redirigerSelonRole(role: string): void {
  const routes: Record<string, string> = {
    ADMINISTRATEUR: '/dashboard/admin',
    TECHNICIEN: '/technicien/bons-travail',
    CLIENT: '/client/dashboard',
    RESPONSABLE_MAINTENANCE: '/dashboard/responsable-maintenance',
    RESPONSABLE_ACHATS: '/dashboard/responsable-achats',
  };
  this.router.navigate([routes[role] ?? '/login']);
}  showPassword = signal(false);
  togglePassword() {
    this.showPassword.update((v) => !v);
  }
}
