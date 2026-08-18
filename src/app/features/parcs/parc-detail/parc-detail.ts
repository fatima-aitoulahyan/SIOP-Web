import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ParcService } from '../services/parc.service';
import { ParcDetailDTO } from '../../../core/models/parc.model';
import { TechnicienService } from '../../../core/services/technicien.service';
import { TechnicienResumeDTO } from '../../../core/models/technicien.model';

@Component({
  selector: 'app-parc-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './parc-detail.html',
  styleUrls: ['./parc-detail.scss'],
})
export class ParcDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private parcService = inject(ParcService);
  private technicienService = inject(TechnicienService);

  parc = signal<ParcDetailDTO | null>(null);
  chargement = signal(true);
  erreur = signal<string | null>(null);
  techniciensDisponibles = signal<TechnicienResumeDTO[]>([]);
  selectedTechnicienId = signal<number | null>(null);

  ngOnInit(): void {
  const id = Number(this.route.snapshot.paramMap.get('id'));
  this.parcService.getById(id).subscribe({
    next: (data) => {
      this.parc.set(data);
      this.chargement.set(false);
      // ─── AJOUTER ICI ───
      this.technicienService.lister().subscribe({
        next: (all) => {
          const idsAssignes = new Set(data.techniciens?.map((t) => t.id) ?? []);
          this.techniciensDisponibles.set(all.filter((t) => !idsAssignes.has(t.id)));
        },
      });
    },
    error: () => {
      this.erreur.set('Impossible de charger le détail du parc.');
      this.chargement.set(false);
    },
  });
}
affecter(): void {
    const techId = this.selectedTechnicienId();
    const parc = this.parc();
    if (!techId || !parc) return;
    this.parcService.affecterTechnicien(parc.id, techId).subscribe({
      next: () => this.ngOnInit(),
      error: () => this.erreur.set("Impossible d'affecter le technicien."),
    });
  }

  retirer(technicienId: number): void {
    const parc = this.parc();
    if (!parc) return;
    this.parcService.retirerTechnicien(parc.id, technicienId).subscribe({
      next: () => this.ngOnInit(),
      error: () => this.erreur.set("Impossible de retirer le technicien."),
    });
  }

}