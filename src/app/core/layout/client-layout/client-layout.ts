import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NotificationBellComponent } from '../notification-bell/notification-bell';

@Component({
  selector: 'app-client-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, NotificationBellComponent],
  templateUrl: './client-layout.html',
  styleUrls: ['./client-layout.scss'],
})
export class ClientLayout {
  private router = inject(Router);

  sidebarReduite = signal(false);

  toggleSidebar(): void {
    this.sidebarReduite.update((val) => !val);
  }

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
