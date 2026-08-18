import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../auth/auth';
import { NotificationBellComponent } from '../notification-bell/notification-bell';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, NotificationBellComponent],
  templateUrl: './admin-layout.html',
  styleUrls: ['./admin-layout.scss'],
})
export class AdminLayoutComponent {
  private router = inject(Router);
  role = inject(AuthService).currentRole;

  demandesOuvert = signal(false);
  sidebarReduite = signal(false);

  toggleDemandes(): void {
    this.demandesOuvert.update((val) => !val);
  }

  toggleSidebar(): void {
    this.sidebarReduite.update((val) => !val);
  }

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
