import { Component, Input, Output, EventEmitter, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AssemblageTree } from '../../../core/models/assemblage.model';
import { AssemblageService } from '../services/assemblage';
import { AssemblageFormComponent } from '../assemblage-form/assemblage-form';
import { ComposantListComponent } from '../composant-list/composant-list';

@Component({
  selector: 'app-assemblage-tree-node',
  standalone: true,
  imports: [CommonModule, AssemblageFormComponent, ComposantListComponent],
  templateUrl: './assemblage-tree-node.html',
  styleUrl: './assemblage-tree-node.scss',
})
export class AssemblageTreeNodeComponent {
  @Input({ required: true }) node!: AssemblageTree;
  @Input({ required: true }) ascenseurId!: number;
  @Output() changed = new EventEmitter<void>();

  private assemblageService = inject(AssemblageService);

  expanded = signal(true);
  showSousZoneForm = signal(false);
  showComposants = signal(false);

  toggle(): void {
    this.expanded.set(!this.expanded());
  }

  onSousZoneAjoutee(): void {
    this.showSousZoneForm.set(false);
    this.changed.emit();
  }

  onSupprimer(): void {
    if (!confirm(`Supprimer "${this.node.nom}" et tout son contenu ?`)) return;
    this.assemblageService.supprimer(this.node.id).subscribe(() => this.changed.emit());
  }
}
