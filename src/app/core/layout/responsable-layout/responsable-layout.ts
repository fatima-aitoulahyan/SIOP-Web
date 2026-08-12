import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../auth/auth';

@Component({
  selector: 'app-responsable-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './responsable-layout.html',
  styleUrls: ['./responsable-layout.scss'],
})
export class ResponsableLayout {
  private router = inject(Router);
  role = inject(AuthService).currentRole;

  // Signal pour gérer l'ouverture/fermeture du menu déroulant des demandes
  demandesOuvert = signal(false);

  toggleDemandes(): void {
    this.demandesOuvert.update((val) => !val);
  }

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
