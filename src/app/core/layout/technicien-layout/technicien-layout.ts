import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { NotificationBellComponent } from '../notification-bell/notification-bell';

@Component({
  selector: 'app-technicien-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, NotificationBellComponent],
  templateUrl: './technicien-layout.html',
  styleUrls: ['./technicien-layout.scss'],
})
export class TechnicienLayoutComponent {
  private router = inject(Router);

  sidebarReduite = signal(false);

  toggleSidebar(): void {
    this.sidebarReduite.update((val) => !val);
  }

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
