import { Component, OnInit, inject, signal, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PieceJointeUploaderComponent } from '../../../pieces-jointes/piece-jointe-uploader/piece-jointe-uploader';
import { DemandeMaintenanceService } from '../../services/demande-maintenance.service';
import { TypeEntiteJointe } from '../../../../core/models/piece-jointe.model';

@Component({
  selector: 'app-creer-demande-evaluation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PieceJointeUploaderComponent],
  templateUrl: './creer-demande-evaluation.html',
  styleUrl: './creer-demande-evaluation.scss',
})
export class CreerDemandeEvaluationComponent implements OnInit {
  @ViewChild('uploaderEvaluation') uploaderEvaluation!: PieceJointeUploaderComponent;

  private fb = inject(FormBuilder);
  private demandeService = inject(DemandeMaintenanceService);
  private router = inject(Router);

  readonly TypeEntiteJointe = TypeEntiteJointe;

  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    ville: ['', [Validators.required, Validators.minLength(2)]],
    adresse: ['', [Validators.required, Validators.minLength(5)]],
    description: ['', [Validators.required, Validators.minLength(10)]],
    dateSouhaitee: [''],
  });

  ngOnInit(): void {}

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.envoiEnCours.set(true);
    this.erreur.set(null);
    const valeurs = this.form.getRawValue();

    this.demandeService
      .creerEvaluation({
        ville: valeurs.ville.trim(),
        adresse: valeurs.adresse.trim(),
        description: valeurs.description.trim(),
        dateSouhaitee: valeurs.dateSouhaitee || null,
      })
      .subscribe({
        next: (res: any) => {
          const idDemande = res?.id ?? res?.data?.id;

          if (!idDemande) {
            console.error("L'ID de la demande est introuvable dans la réponse :", res);
            this.envoiEnCours.set(false);
            this.router.navigate(['/client/demandes']);
            return;
          }

          this.uploaderEvaluation.uploaderFichiersEnAttente(idDemande).subscribe({
            next: () => {
              this.envoiEnCours.set(false);
              this.router.navigate(['/client/demandes']);
            },
            error: () => {
              this.envoiEnCours.set(false);
              this.router.navigate(['/client/demandes']);
            },
          });
        },
        error: (err) => {
          this.erreur.set(
            err?.error?.message ??
              "La demande d'évaluation a échoué. Vérifiez les informations saisies.",
          );
          this.envoiEnCours.set(false);
        },
      });
  }
}
