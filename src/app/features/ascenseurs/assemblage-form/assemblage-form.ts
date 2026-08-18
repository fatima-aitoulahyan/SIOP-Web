import { Component, Input, Output, EventEmitter, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AssemblageService } from '../services/assemblage';
import { TypeAssemblage } from '../../../core/models/assemblage.model';

@Component({
  selector: 'app-assemblage-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './assemblage-form.html',
  styleUrl: './assemblage-form.scss',
})
export class AssemblageFormComponent {
  @Input({ required: true }) ascenseurId!: number;
  @Input() parentId: number | null = null;
  @Output() created = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private assemblageService = inject(AssemblageService);

  loading = signal(false);
  errorMessage = signal<string | null>(null);

  types: TypeAssemblage[] = ['CABINE', 'GAINE', 'MACHINERIE', 'PORTE', 'ELECTRIQUE', 'AUTRE'];

  form = this.fb.group({
    nom: ['', Validators.required],
    reference: ['', Validators.required],
    type: ['CABINE' as TypeAssemblage, Validators.required],
    fabricant: [''],
    dateInstallation: [''],
    dureeVieEstimeeMois: [null as number | null],
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    this.assemblageService
      .creer({
        nom: this.form.value.nom!,
        reference: this.form.value.reference!,
        type: this.form.value.type!,
        fabricant: this.form.value.fabricant || undefined,
        dateInstallation: this.form.value.dateInstallation || undefined,
        dureeVieEstimeeMois: this.form.value.dureeVieEstimeeMois ?? undefined,
        ascenseurId: this.ascenseurId,
        parentId: this.parentId ?? undefined,
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
