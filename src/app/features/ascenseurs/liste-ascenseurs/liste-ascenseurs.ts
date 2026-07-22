import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AscenseurService } from '../services/ascenseur.service';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';

@Component({
  selector: 'app-liste-ascenseurs',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './liste-ascenseurs.html',
  styleUrls: ['./liste-ascenseurs.scss'],
})
export class ListeAscenseurs implements OnInit {
  private ascenseurService = inject(AscenseurService);

  ascenseurs = signal<AscenseurDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    this.ascenseurService.listerTous().subscribe({
      next: (res) => {
        this.ascenseurs.set(res.data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger la liste des ascenseurs.');
        this.chargement.set(false);
      },
    });
  }
}
