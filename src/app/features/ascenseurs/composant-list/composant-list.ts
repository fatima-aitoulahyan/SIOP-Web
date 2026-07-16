import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ComposantService } from '../services/composant';
import { Composant, TypeComposant } from '../../../core/models/composant.model';

@Component({
  selector: 'app-composant-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './composant-list.html',
  styleUrl: './composant-list.scss',
})
export class ComposantListComponent implements OnInit {
  @Input({ required: true }) assemblageId!: number;
  @Input() assemblageNom?: string;

  private fb = inject(FormBuilder);
  private composantService = inject(ComposantService);

  composants = signal<Composant[]>([]);
  showForm = signal(false);
  loading = signal(false);

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

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.composantService.listerParAssemblage(this.assemblageId).subscribe((res) => {
      this.composants.set(res.data);
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    this.composantService
      .creer({
        ...(this.form.value as any),
        assemblageId: this.assemblageId,
      })
      .subscribe(() => {
        this.loading.set(false);
        this.showForm.set(false);
        this.form.reset({ type: 'MOTEUR' });
        this.charger();
      });
  }

  onSupprimer(id: number): void {
    if (!confirm('Supprimer ce composant ?')) return;
    this.composantService.supprimer(id).subscribe(() => this.charger());
  }
}
