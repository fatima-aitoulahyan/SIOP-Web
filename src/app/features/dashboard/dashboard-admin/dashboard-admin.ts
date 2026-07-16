import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth';
import { UtilisateurService } from '../../utilisateurs/services/utilisateur';
import { UtilisateurResponseDTO, TypeUtilisateur } from '../../../core/models/utilisateur.model';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.scss',
})
export class DashboardAdmin implements OnInit {
  private authService = inject(AuthService);
  private utilisateurService = inject(UtilisateurService);

  loading = signal(false);
  errorMessage = signal<string | null>(null);
  utilisateurs = signal<UtilisateurResponseDTO[]>([]);

  totalUtilisateurs = computed(() => this.utilisateurs().length);
  utilisateursActifs = computed(() => this.utilisateurs().filter((u) => u.actif).length);
  totalTechniciens = computed(
    () => this.utilisateurs().filter((u) => u.type === TypeUtilisateur.TECHNICIEN).length,
  );
  totalClients = computed(
    () => this.utilisateurs().filter((u) => u.type === TypeUtilisateur.CLIENT).length,
  );

  derniersUtilisateurs = computed(() =>
    [...this.utilisateurs()]
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5),
  );

  ngOnInit() {
    this.charger();
  }

  charger() {
    this.loading.set(true);
    this.utilisateurService.getAll().subscribe({
      next: (data) => {
        this.utilisateurs.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message || 'Erreur de chargement.');
        this.loading.set(false);
      },
    });
  }

  logout() {
    this.authService.logout();
  }
}
