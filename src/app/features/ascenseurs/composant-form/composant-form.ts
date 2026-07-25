import { Component, Input, Output, EventEmitter, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComposantService } from '../services/composant';
import { TypeComposant } from '../../../core/models/composant.model';

@Component({
  selector: 'app-composant-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './composant-form.html',
  styleUrl: './composant-form.scss',
})
export class ComposantFormComponent {
  @Input({ required: true }) assemblageId!: number;
  @Output() created = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private composantService = inject(ComposantService);

  loading = signal(false);
  errorMessage = signal<string | null>(null);

  types: TypeComposant[] = [
    'MOTEUR',
    'FREIN',
    'CABLE',
    'CABINE',
    'PORTE',
    'CONTROLEUR',
    'CAPTEUR',
    'AUTRE',
  ];

  form = this.fb.group({
    nom: ['', Validators.required],
    reference: ['', Validators.required],
    type: ['MOTEUR' as TypeComposant, Validators.required],
    fabricant: [''],
    dateInstallation: [''],
    dureeVieEstimeeMois: [null as number | null],
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    this.composantService
      .creer({
        nom: this.form.value.nom!,
        reference: this.form.value.reference!,
        type: this.form.value.type!,
        fabricant: this.form.value.fabricant || undefined,
        dateInstallation: this.form.value.dateInstallation || undefined,
        dureeVieEstimeeMois: this.form.value.dureeVieEstimeeMois ?? undefined,
        assemblageId: this.assemblageId,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.created.emit();
        },
        error: (err) => {
          this.loading.set(false);
          this.errorMessage.set(err.error?.message ?? 'Erreur lors de la création.');
        },
      });
  }
}
