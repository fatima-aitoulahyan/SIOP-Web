import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AscenseurService } from '../services/ascenseur.service';
import { AssemblageService } from '../services/assemblage';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';
import { AssemblageTree } from '../../../core/models/assemblage.model';
import { AssemblageFormComponent } from '../assemblage-form/assemblage-form';
import { AssemblageExplorerComponent } from '../assemblage-explorer/assemblage-explorer';

@Component({
  selector: 'app-ascenseur-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, AssemblageFormComponent, AssemblageExplorerComponent],
  templateUrl: './ascenseur-detail.html',
  styleUrl: './ascenseur-detail.scss',
})
export class AscenseurDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private ascenseurService = inject(AscenseurService);
  private assemblageService = inject(AssemblageService);

  ascenseur = signal<AscenseurDTO | null>(null);
  arbre = signal<AssemblageTree[]>([]);
  activeTab = signal<'info' | 'arborescence'>('info');
  showFormRacine = signal(false);

  // true uniquement lors du tout premier chargement
  chargementInitial = signal(true);
  // true à chaque rechargement (premier ou suivant), pour un indicateur discret
  rafraichissementEnCours = signal(false);

  generationEnCours = signal(false);
  ascenseurId!: number;

  ngOnInit(): void {
    this.ascenseurId = Number(this.route.snapshot.paramMap.get('id'));
    this.chargerAscenseur();
    this.chargerArbre();
  }

  chargerAscenseur(): void {
    this.ascenseurService.getById(this.ascenseurId).subscribe((res) => {
      this.ascenseur.set(res.data);
    });
  }

  chargerArbre(): void {
    this.rafraichissementEnCours.set(true);
    this.assemblageService.arbreParAscenseur(this.ascenseurId).subscribe({
      next: (res) => {
        this.arbre.set(res.data);
        this.chargementInitial.set(false);
        this.rafraichissementEnCours.set(false);
      },
      error: () => {
        this.chargementInitial.set(false);
        this.rafraichissementEnCours.set(false);
      },
    });
  }

  genererArborescence(): void {
    this.generationEnCours.set(true);
    this.assemblageService.genererArborescence(this.ascenseurId).subscribe({
      next: () => {
        this.generationEnCours.set(false);
        this.chargerArbre();
      },
      error: (err) => {
        this.generationEnCours.set(false);
        console.error('Erreur génération arborescence', err);
      },
    });
  }

  onZoneRacineAjoutee(): void {
    this.showFormRacine.set(false);
    this.chargerArbre();
  }
}
