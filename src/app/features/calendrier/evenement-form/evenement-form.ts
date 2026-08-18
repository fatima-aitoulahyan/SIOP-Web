import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

import { CalendrierService } from '../services/calendrier.service';
import {
  CalendrierEventDTO,
  EvenementRequestDTO,
  TypeEvenementCalendrier,
} from '../models/calendrier-event.model';

@Component({
  selector: 'app-evenement-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './evenement-form.html',
  styleUrl: './evenement-form.scss',
})
export class EvenementForm implements OnInit {
  private fb = inject(FormBuilder);
  private calendrierService = inject(CalendrierService);

  @Input() dateInitiale: string | null = null;
  @Input() evenement: CalendrierEventDTO | null = null;

  @Output() fermer = new EventEmitter<void>();
  @Output() enregistre = new EventEmitter<void>();
  @Output() supprime = new EventEmitter<number>(); // <-- Ajout de l'output supprime

  submitting = signal(false);
  erreur = signal<string | null>(null);

  types: TypeEvenementCalendrier[] = ['REUNION', 'CONGE', 'FORMATION', 'AUTRE'];

  form = this.fb.nonNullable.group({
    titre: ['', Validators.required],
    description: [''],
    type: ['REUNION' as TypeEvenementCalendrier, Validators.required],
    dateDebut: ['', Validators.required],
    dateFin: ['', Validators.required],
    lieu: [''],
  });

  ngOnInit(): void {
    if (this.evenement) {
      this.form.patchValue({
        titre: this.evenement.titre,
        type: this.evenement.type as TypeEvenementCalendrier,
        dateDebut: this.toLocalInput(this.evenement.debut),
        dateFin: this.toLocalInput(this.evenement.fin),
        lieu: this.evenement.lieu ?? '',
      });
    } else if (this.dateInitiale) {
      const debut = this.toLocalInput(this.dateInitiale);
      this.form.patchValue({ dateDebut: debut, dateFin: debut });
    }
  }

  private toLocalInput(iso: string): string {
    return iso.length >= 16 ? iso.substring(0, 16) : iso;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.erreur.set(null);

    const raw = this.form.getRawValue();
    const dto: EvenementRequestDTO = {
      titre: raw.titre,
      description: raw.description || undefined,
      type: raw.type,
      dateDebut: raw.dateDebut,
      dateFin: raw.dateFin,
      lieu: raw.lieu || undefined,
      technicienIds: [],
    };

    const requete = this.evenement
      ? this.calendrierService.modifierEvenement(
          Number(String(this.evenement.id).replace('EVT-', '')),
          dto,
        )
      : this.calendrierService.creerEvenement(dto);

    requete.subscribe({
      next: () => {
        this.submitting.set(false);
        this.enregistre.emit();
      },
      error: (err) => {
        this.submitting.set(false);
        this.erreur.set(err?.error?.message ?? "Erreur lors de l'enregistrement.");
      },
    });
  }

  onSupprimer(): void {
    if (this.evenement && this.evenement.id != null) {
      const idNum = Number(String(this.evenement.id).replace('EVT-', ''));
      this.supprime.emit(idNum);
    }
  }

  onAnnuler(): void {
    this.fermer.emit();
  }
}
