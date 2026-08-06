import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParcDTO } from '../../../core/models/parc.model';
import { ParcService } from '../services/parc.service';

@Component({
  selector: 'app-parc-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './parc-list.html',
  styleUrls: ['./parc-list.scss'],
})
export class ParcListComponent implements OnInit {
  private parcService = inject(ParcService);

  parcs = signal<ParcDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  parcASupprimer = signal<ParcDTO | null>(null);
  suppressionEnCours = signal(false);

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.parcService.listerTous().subscribe({
      next: (data) => {
        this.parcs.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger la liste des parcs.');
        this.chargement.set(false);
      },
    });
  }

  demanderSuppression(parc: ParcDTO): void {
    this.parcASupprimer.set(parc);
  }

  annulerSuppression(): void {
    this.parcASupprimer.set(null);
  }

  confirmerSuppression(): void {
    const parc = this.parcASupprimer();
    if (!parc) return;

    this.suppressionEnCours.set(true);
    this.parcService.supprimer(parc.id).subscribe({
      next: () => {
        this.parcs.update((liste) => liste.filter((p) => p.id !== parc.id));
        this.suppressionEnCours.set(false);
        this.parcASupprimer.set(null);
      },
      error: () => {
        this.erreur.set('La suppression a échoué. Le parc contient peut-être encore des sites.');
        this.suppressionEnCours.set(false);
        this.parcASupprimer.set(null);
      },
    });
  }
}