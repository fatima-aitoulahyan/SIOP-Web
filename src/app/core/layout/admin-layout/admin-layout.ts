import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, inject, signal } from '@angular/core';
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
  private elementRef = inject(ElementRef);
  role = inject(AuthService).currentRole;

  demandesOuvert = signal(false);
  sidebarReduite = signal(false);

  toggleDemandes(): void {
    if (this.sidebarReduite()) {
      this.sidebarReduite.set(false);
      this.demandesOuvert.set(true);
    } else {
      this.demandesOuvert.update((val) => !val);
    }
  }

  toggleSidebar(): void {
    this.sidebarReduite.update((val) => !val);
  }

  deconnexion(): void {
    this.router.navigate(['/login']);
  }

  // Ferme le popover "Demandes" si on clique en dehors du menu (comportement type menu contextuel, comme sur l'image 1)
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (this.demandesOuvert() && !this.elementRef.nativeElement.contains(event.target)) {
      this.demandesOuvert.set(false);
    }
  }

  // Referme le popover si l'écran est redimensionné (rotation du téléphone, etc.)
  @HostListener('window:resize')
  onResize(): void {
    if (this.demandesOuvert()) {
      this.demandesOuvert.set(false);
    }
  }
}
