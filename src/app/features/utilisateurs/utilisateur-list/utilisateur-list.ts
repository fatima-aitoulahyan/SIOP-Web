import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UtilisateurService } from '../services/utilisateur';
import {
  UtilisateurResponseDTO,
  TYPE_UTILISATEUR_LABELS,
} from '../../../core/models/utilisateur.model';

@Component({
  selector: 'app-utilisateur-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './utilisateur-list.html',
  styleUrl: './utilisateur-list.scss',
})
export class UtilisateurList implements OnInit {
  private utilisateurService = inject(UtilisateurService);

  utilisateurs = signal<UtilisateurResponseDTO[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  labels = TYPE_UTILISATEUR_LABELS;

  ngOnInit() {
    this.charger();
  }

  charger() {
    this.loading.set(true);
    this.utilisateurService.getAll().subscribe({
      next: (data) => {
        this.utilisateurs.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message || 'Erreur de chargement.');
        this.loading.set(false);
      },
    });
  }

  desactiver(id: number) {
    if (!confirm('Désactiver cet utilisateur ?')) return;
    this.utilisateurService.desactiver(id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage.set(err?.error?.message || 'Erreur.'),
    });
  }

  supprimer(id: number) {
    if (!confirm('Supprimer définitivement cet utilisateur ?')) return;
    this.utilisateurService.supprimer(id).subscribe({
      next: () => this.charger(),
      error: (err) => this.errorMessage.set(err?.error?.message || 'Erreur.'),
    });
  }
}
