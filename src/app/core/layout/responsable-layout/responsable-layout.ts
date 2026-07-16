import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-responsable-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './responsable-layout.html',
  styleUrls: ['./responsable-layout.scss'],
})
export class ResponsableLayout {
  private router = inject(Router);

  deconnexion(): void {
    this.router.navigate(['/login']);
  }
}
