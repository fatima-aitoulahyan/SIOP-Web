import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
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

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
