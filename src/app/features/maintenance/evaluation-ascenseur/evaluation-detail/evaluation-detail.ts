import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EvaluationAscenseurService } from '../../services/evaluation-ascenseur.service';
import { EvaluationAscenseurDTO } from '../../../../core/models/evaluation-ascenseur.model';

@Component({
  selector: 'app-evaluation-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './evaluation-detail.html',
  styleUrls: ['./evaluation-detail.scss'],
})
export class EvaluationDetailComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private evaluationService = inject(EvaluationAscenseurService);

  evaluation = signal<EvaluationAscenseurDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  envoiEnCours = signal(false);
  showRejetForm = signal(false);

  rejetForm = this.fb.nonNullable.group({
    motif: ['', [Validators.required, Validators.minLength(10)]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = Number(idParam);
    if (!idParam || !Number.isInteger(id)) {
      this.erreur.set('Identifiant invalide.');
      this.chargement.set(false);
      return;
    }
    this.evaluationService.getById(id).subscribe({
      next: (data) => {
        this.evaluation.set(data);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set("Impossible de charger l'évaluation.");
        this.chargement.set(false);
      },
    });
  }

  accepter(): void {
    const e = this.evaluation();
    if (!e) return;
    this.envoiEnCours.set(true);
    this.evaluationService.valider(e.id, { accepter: true }).subscribe({
      next: () => {
        this.envoiEnCours.set(false);
        this.router.navigate(['/responsable/evaluations']);
      },
      error: (err) => {
        this.envoiEnCours.set(false);
        this.erreur.set(err?.error?.message ?? "L'acceptation a échoué.");
      },
    });
  }

  rejeter(): void {
    if (this.rejetForm.invalid) {
      this.rejetForm.markAllAsTouched();
      return;
    }
    const e = this.evaluation();
    if (!e) return;

    this.envoiEnCours.set(true);
    this.evaluationService
      .valider(e.id, { accepter: false, motif: this.rejetForm.getRawValue().motif })
      .subscribe({
        next: () => {
          this.envoiEnCours.set(false);
          this.router.navigate(['/responsable/evaluations']);
        },
        error: (err) => {
          this.envoiEnCours.set(false);
          this.erreur.set(err?.error?.message ?? 'Le rejet a échoué.');
        },
      });
  }
}
