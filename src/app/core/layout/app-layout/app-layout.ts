import { CommonModule } from '@angular/common';
import { Component, inject, signal, computed, effect, untracked } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../auth/auth';
import { ProfilDTO, TYPE_UTILISATEUR_LABELS, TypeUtilisateur } from '../../models/utilisateur.model';

@Component({
  selector: 'app-app-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app-layout.html',
  styleUrl: './app-layout.scss',
})
export class AppLayout {
  private router = inject(Router);
  private authService = inject(AuthService);

  role = this.authService.currentRole;

  profil = signal<ProfilDTO | null>(null);

  demandesOuvert = signal(false);

  initiales = computed(() => {
    const p = this.profil();
    if (!p) return '?';
    return `${p.prenom?.[0] ?? ''}${p.nom?.[0] ?? ''}`.toUpperCase();
  });

  roleLabel = computed(() => {
    const role = this.profil()?.role as TypeUtilisateur | undefined;
    return (role && TYPE_UTILISATEUR_LABELS[role]) || this.profil()?.role || '';
  });

  cta = computed(() => {
    const routes: Record<string, { label: string; lien: string } | null> = {
      CLIENT: { label: 'Nouvelle Demande', lien: '/client/demandes/nouveau' },
      RESPONSABLE_MAINTENANCE: { label: 'Nouveau bon de travail', lien: '/responsable/bons-travail/nouveau' },
      ADMINISTRATEUR: null,
      TECHNICIEN: null,
    };
    return routes[this.role() ?? ''] ?? null;
  });

  constructor() {
    effect(
      () => {
        if (!this.authService.authReady()) return;
        untracked(() => this.chargerProfil());
      },
      { allowSignalWrites: true },
    );
  }

  private chargerProfil(): void {
    this.authService.monProfil().subscribe({
      next: (p) => this.profil.set(p),
      error: () => undefined,
    });
  }

  toggleDemandes(): void {
    this.demandesOuvert.update((val) => !val);
  }

  aider(): void {
    return;
  }

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
