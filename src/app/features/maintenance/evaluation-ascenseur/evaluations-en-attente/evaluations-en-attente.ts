import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EvaluationAscenseurDTO } from '../../../../core/models/evaluation-ascenseur.model';
import { EvaluationAscenseurService } from '../../services/evaluation-ascenseur.service';

@Component({
  selector: 'app-evaluations-en-attente',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './evaluations-en-attente.html',
  styleUrls: ['./evaluations-en-attente.scss'],
})
export class EvaluationsEnAttenteComponent implements OnInit {
  private evaluationService = inject(EvaluationAscenseurService);

  evaluations = signal<EvaluationAscenseurDTO[]>([]);
  chargement = signal(true);
  erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.evaluationService.getEnAttenteValidation().subscribe({
      next: (data) => {
        this.evaluations.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger les évaluations en attente.');
        this.chargement.set(false);
      },
    });
  }
}
