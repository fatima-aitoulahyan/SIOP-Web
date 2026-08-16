import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { NotificationBellComponent } from '../notification-bell/notification-bell';
// Importez votre AuthService si nécessaire :
// import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-technicien-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, NotificationBellComponent],
  templateUrl: './technicien-layout.html',
  styleUrls: ['./technicien-layout.scss'],
})
export class TechnicienLayoutComponent {
  private router = inject(Router);
  // private authService = inject(AuthService);

  sidebarReduite = signal(false);

  // Exemple si vous souhaitez lier le rôle dynamiquement
  // role = this.authService.getRole;

  toggleSidebar(): void {
    this.sidebarReduite.update((val) => !val);
  }

  deconnexion(): void {
    // Nettoyez le stockage local si nécessaire (ex: localStorage.removeItem('token'))
    this.router.navigate(['/login']);
  }
}
