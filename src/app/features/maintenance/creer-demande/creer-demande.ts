import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';
import {
  PrioriteDemande,
  PRIORITE_DEMANDE_LABELS,
  TypeDemande,
  TYPE_DEMANDE_LABELS,
} from '../../../core/models/demande-maintenance.model';
import { AscenseurService } from '../../ascenseurs/services/ascenseur.service';
import { DemandeMaintenanceService } from '../services/demande-maintenance.service';

// ⚠️ Adapter ces chemins selon l'emplacement réel de tes fichiers
import { PieceJointeUploaderComponent } from '../../pieces-jointes/piece-jointe-uploader/piece-jointe-uploader';
import { TypeEntiteJointe } from '../../../core/models/piece-jointe.model';

@Component({
  selector: 'app-creer-demande',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PieceJointeUploaderComponent],
  templateUrl: './creer-demande.html',
  styleUrl: './creer-demande.scss',
})
export class CreerDemandeComponent implements OnInit {
  @ViewChild('uploaderDemande') uploaderDemande!: PieceJointeUploaderComponent;

  private fb = inject(FormBuilder);
  private demandeService = inject(DemandeMaintenanceService);
  private ascenseurService = inject(AscenseurService);
  private router = inject(Router);

  readonly types = Object.values(TypeDemande);
  readonly priorites = Object.values(PrioriteDemande);
  readonly typeLabels = TYPE_DEMANDE_LABELS;
  readonly prioriteLabels = PRIORITE_DEMANDE_LABELS;

  // Exposé pour le template : évite de répéter "TypeEntiteJointe" partout
  readonly TypeEntiteJointe = TypeEntiteJointe;

  ascenseurs = signal<AscenseurDTO[]>([]);
  chargementAscenseurs = signal(true);
  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    ascenseurId: [null as number | null, Validators.required],
    typeDemande: [null as TypeDemande | null, Validators.required],
    priorite: [PrioriteDemande.NORMALE as PrioriteDemande | null, Validators.required],
    description: ['', [Validators.required, Validators.minLength(10)]],
    dateSouhaitee: [''],
  });

  ngOnInit(): void {
    this.chargerAscenseurs();
  }

  private chargerAscenseurs(): void {
    this.chargementAscenseurs.set(true);
    this.ascenseurService.mesAscenseurs().subscribe({
      next: (res) => {
        this.ascenseurs.set(res.data ?? []);
        this.chargementAscenseurs.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger vos ascenseurs.');
        this.chargementAscenseurs.set(false);
      },
    });
  }

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.envoiEnCours.set(true);
    this.erreur.set(null);
    const valeurs = this.form.getRawValue();

    this.demandeService
      .creer({
        ascenseurId: valeurs.ascenseurId!,
        typeDemande: valeurs.typeDemande!,
        priorite: valeurs.priorite!,
        description: valeurs.description.trim(),
        dateSouhaitee: valeurs.dateSouhaitee || null,
      })
      .subscribe({
        next: (res) => {
          // Upload des pièces jointes sélectionnées avant la création de la demande
          this.uploaderDemande.uploaderFichiersEnAttente(res.id);
          this.router.navigate(['/maintenance']);
        },
        error: (err) => {
          this.erreur.set(
            err?.error?.message ??
              'La création de la demande a échoué. Vérifiez les informations saisies.',
          );
          this.envoiEnCours.set(false);
        },
      });
  }
}
