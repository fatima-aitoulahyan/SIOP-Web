import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EvaluationAscenseurService } from '../../services/evaluation-ascenseur.service';
import { TypeAscenseur } from '../../../../core/models/ascenseur.model';

@Component({
  selector: 'app-evaluation-ascenseur-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './evaluation-ascenseur-form.html',
  styleUrls: ['./evaluation-ascenseur-form.scss'],
})
export class EvaluationAscenseurFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private evaluationService = inject(EvaluationAscenseurService);

  bonTravailId = signal<number | null>(null);
  evaluationId = signal<number | null>(null);
  chargement = signal(true);
  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);

  types = Object.values(TypeAscenseur);

  form = this.fb.nonNullable.group({
    fabricant: ['', [Validators.required]],
    nom: [''],
    marque: [''],
    modele: [''],
    numeroSerie: [''],
    codeBarre: [''],
    nombreEtages: [null as number | null],
    capacitePersonnes: [null as number | null],
    chargeMaxKg: [null as number | null],
    vitesse: [null as number | null],
    puissance: [''],
    type: [null as TypeAscenseur | null],
    dateMiseEnService: [''],
    etatPortes: [''],
    positionCabine: [''],
    anomalies: [''],
    causeExterieure: [''],
    observations: [''],
  });

  ngOnInit(): void {
    const btIdParam = this.route.snapshot.paramMap.get('bonTravailId');
    const btId = Number(btIdParam);
    if (!btIdParam || !Number.isInteger(btId)) {
      this.erreur.set('Bon de travail invalide.');
      this.chargement.set(false);
      return;
    }
    this.bonTravailId.set(btId);

    // On tente de récupérer une évaluation déjà existante (brouillon repris)
    this.evaluationService.getByBonTravailId(btId).subscribe({
      next: (evalu) => {
        this.evaluationId.set(evalu.id);
        this.form.patchValue({
          fabricant: evalu.fabricant ?? '',
          nom: evalu.nom ?? '',
          marque: evalu.marque ?? '',
          modele: evalu.modele ?? '',
          numeroSerie: evalu.numeroSerie ?? '',
          codeBarre: evalu.codeBarre ?? '',
          nombreEtages: evalu.nombreEtages,
          capacitePersonnes: evalu.capacitePersonnes,
          chargeMaxKg: evalu.chargeMaxKg,
          vitesse: evalu.vitesse,
          puissance: evalu.puissance ?? '',
          type: evalu.type,
          dateMiseEnService: evalu.dateMiseEnService ?? '',
          etatPortes: evalu.etatPortes ?? '',
          positionCabine: evalu.positionCabine ?? '',
          anomalies: evalu.anomalies ?? '',
          causeExterieure: evalu.causeExterieure ?? '',
          observations: evalu.observations ?? '',
        });
        this.chargement.set(false);
      },
      error: () => {
        // Pas encore de brouillon : on en crée un
        this.evaluationService.creerBrouillon(btId).subscribe({
          next: (evalu) => {
            this.evaluationId.set(evalu.id);
            this.chargement.set(false);
          },
          error: () => {
            this.erreur.set("Impossible de créer le brouillon d'évaluation.");
            this.chargement.set(false);
          },
        });
      },
    });
  }

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const evalId = this.evaluationId();
    if (!evalId) return;

    this.envoiEnCours.set(true);
    this.erreur.set(null);

    this.evaluationService.soumettre(evalId, this.form.getRawValue()).subscribe({
      next: () => {
        this.envoiEnCours.set(false);
        this.router.navigate(['/technicien/bons-travail']);
      },
      error: (err) => {
        this.envoiEnCours.set(false);
        this.erreur.set(err?.error?.message ?? "L'envoi de l'évaluation a échoué.");
      },
    });
  }
}
