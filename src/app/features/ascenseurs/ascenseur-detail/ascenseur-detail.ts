import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AscenseurService } from '../services/ascenseur.service';
import { AssemblageService } from '../services/assemblage';
import { PieceJointeService } from '../../pieces-jointes/services/piece-jointe';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';
import { AssemblageTree } from '../../../core/models/assemblage.model';
import { PieceJointeDTO, TypeEntiteJointe } from '../../../core/models/piece-jointe.model';
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
  private pieceJointeService = inject(PieceJointeService);

  ascenseur = signal<AscenseurDTO | null>(null);
  arbre = signal<AssemblageTree[]>([]);
  activeTab = signal<'info' | 'arborescence'>('info');
  showFormRacine = signal(false);

  chargementInitial = signal(true);
  rafraichissementEnCours = signal(false);
  generationEnCours = signal(false);

  // Pièces jointes
  piecesJointes = signal<PieceJointeDTO[]>([]);
  chargementPiecesJointes = signal(false);

  ascenseurId!: number;

  ngOnInit(): void {
    this.ascenseurId = Number(this.route.snapshot.paramMap.get('id'));
    this.chargerAscenseur();
    this.chargerArbre();
    this.chargerPiecesJointes();
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

  chargerPiecesJointes(): void {
    this.chargementPiecesJointes.set(true);
    this.pieceJointeService.lister(TypeEntiteJointe.ASCENSEUR, this.ascenseurId).subscribe({
      next: (res) => {
        this.piecesJointes.set(res.data);
        this.chargementPiecesJointes.set(false);
      },
      error: (err) => {
        console.error('Erreur chargement pièces jointes', err);
        this.chargementPiecesJointes.set(false);
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
