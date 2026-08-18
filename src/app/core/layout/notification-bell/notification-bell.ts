import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AppNotification } from '../../models/notification.model';
import { NotificationService } from './service/notification.service';
import { AuthService } from '../../auth/auth';

@Component({
  selector: 'app-notification-bell',
  templateUrl: './notification-bell.html',
  styleUrls: ['./notification-bell.scss'],
  imports: [CommonModule],
})
export class NotificationBellComponent implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);

  notifications: AppNotification[] = [];
  nonLuesCount = 0;
  panelOuvert = false;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.nonLuesCount$.subscribe((count) => (this.nonLuesCount = count));
  }

  togglePanel(): void {
    this.panelOuvert = !this.panelOuvert;
    if (this.panelOuvert) {
      this.notificationService.lister().subscribe((notifs) => (this.notifications = notifs));
    }
  }

  ouvrirNotification(n: AppNotification): void {
    if (!n.lu) {
      this.notificationService.marquerLue(n.id).subscribe(() => {
        n.lu = true;
        this.notificationService.rafraichir();
      });
    }

    this.panelOuvert = false;
    this.naviguerVersEntite(n);
  }

  private naviguerVersEntite(n: AppNotification): void {
    const role = this.authService.currentRole();

    if (n.entiteType === 'DEMANDE_MAINTENANCE') {
      const base: Record<string, string> = {
        CLIENT: '/client/demandes',
        RESPONSABLE_MAINTENANCE: '/responsable/demandes',
        TECHNICIEN: '/technicien/demandes',
      };
      const path = base[role as string];
      if (path) this.router.navigate([path, n.entiteId]);
    } else if (n.entiteType === 'BON_TRAVAIL') {
      const base: Record<string, string> = {
        RESPONSABLE_MAINTENANCE: '/responsable/bons-travail',
        TECHNICIEN: '/technicien/bons-travail',
      };
      const path = base[role as string];
      if (path) this.router.navigate([path, n.entiteId]);
    }
  }
}
