import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AscenseurService } from '../services/ascenseur.service';
import { AssemblageService } from '../services/assemblage';
import { AscenseurDTO } from '../../../core/models/ascenseur.model';
import { AssemblageTree } from '../../../core/models/assemblage.model';
import { AssemblageTreeNodeComponent } from '../assemblage-tree-node/assemblage-tree-node';
import { AssemblageFormComponent } from '../assemblage-form/assemblage-form';

@Component({
  selector: 'app-ascenseur-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, AssemblageTreeNodeComponent, AssemblageFormComponent],
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
    this.assemblageService.arbreParAscenseur(this.ascenseurId).subscribe((res) => {
      this.arbre.set(res.data);
    });
  }

  onZoneRacineAjoutee(): void {
    this.showFormRacine.set(false);
    this.chargerArbre();
  }
}
