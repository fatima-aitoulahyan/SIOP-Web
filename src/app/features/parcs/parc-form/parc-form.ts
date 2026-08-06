import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ParcService } from '../services/parc.service';

@Component({
  selector: 'app-parc-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './parc-form.html',
  styleUrls: ['./parc-form.scss'],
})
export class ParcFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private parcService = inject(ParcService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  modeEdition = signal(false);
  parcId = signal<number | null>(null);
  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nom: ['', [Validators.required, Validators.maxLength(100)]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.modeEdition.set(true);
      this.parcId.set(id);

      this.parcService.getById(id).subscribe({
        next: (parc) => {
          this.form.patchValue({ nom: parc.nom });
        },
        error: () => this.erreur.set('Impossible de charger le parc.'),
      });
    }
  }

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.envoiEnCours.set(true);
    this.erreur.set(null);
    const valeurs = this.form.getRawValue();

    if (this.modeEdition() && this.parcId() !== null) {
      this.parcService.modifier(this.parcId()!, { nom: valeurs.nom }).subscribe({
        next: () => this.router.navigate(['/parcs']),
        error: () => {
          this.erreur.set('La modification du parc a échoué.');
          this.envoiEnCours.set(false);
        },
      });
    } else {
      this.parcService.creer({ nom: valeurs.nom }).subscribe({
        next: () => this.router.navigate(['/parcs']),
        error: () => {
          this.erreur.set('La création du parc a échoué.');
          this.envoiEnCours.set(false);
        },
      });
    }
  }
}