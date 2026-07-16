import { Component, Input, Output, EventEmitter, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AssemblageService } from '../services/assemblage';

@Component({
  selector: 'app-assemblage-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './assemblage-form.html',
  styleUrl: './assemblage-form.scss',
})
export class AssemblageFormComponent {
  @Input({ required: true }) ascenseurId!: number;
  @Input({ required: true }) niveau!: number;
  @Input() parentId: number | null = null;
  @Output() created = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private assemblageService = inject(AssemblageService);

  loading = signal(false);
  errorMessage = signal<string | null>(null);

  form = this.fb.group({
    nom: ['', Validators.required],
    description: [''],
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    this.assemblageService
      .creer({
        nom: this.form.value.nom!,
        description: this.form.value.description ?? undefined,
        niveau: this.niveau,
        ascenseurId: this.ascenseurId,
        parentId: this.parentId,
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
