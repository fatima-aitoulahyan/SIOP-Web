import {
  Component,
  Input,
  Output,
  EventEmitter,
  signal,
  computed,
  inject,
  ViewChild,
  ElementRef,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { AssemblageTree } from '../../../core/models/assemblage.model';
import { Composant } from '../../../core/models/composant.model';
import { AssemblageService } from '../services/assemblage';
import { ComposantService } from '../services/composant';
import { AssemblageFormComponent } from '../assemblage-form/assemblage-form';
import { ComposantFormComponent } from '../composant-form/composant-form';

@Component({
  selector: 'app-assemblage-explorer',
  standalone: true,
  imports: [CommonModule, AssemblageFormComponent, ComposantFormComponent],
  templateUrl: './assemblage-explorer.html',
  styleUrl: './assemblage-explorer.scss',
})
export class AssemblageExplorerComponent implements OnChanges {
  @Input({ required: true }) nodes!: AssemblageTree[];
  @Input({ required: true }) ascenseurId!: number;
  @Output() changed = new EventEmitter<void>();

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private assemblageService = inject(AssemblageService);
  private composantService = inject(ComposantService);

  // copie interne réactive de l'@Input, pour que les computed() réagissent bien
  private nodesSignal = signal<AssemblageTree[]>([]);

  // chemin de navigation stocké par ID (survit aux rechargements de données)
  path = signal<number[]>([]);

  showSousZoneForm = signal(false);
  suppressionEnCours = signal<number | null>(null);
  erreurSuppression = signal<string | null>(null);
  showComposantForm = signal(false);

  private cibleUpload: { type: 'assemblage' | 'composant'; id: number } | null = null;

  uploadEnCours = signal<{ type: 'assemblage' | 'composant'; id: number } | null>(null);
  erreurUpload = signal<string | null>(null);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['nodes']) {
      this.nodesSignal.set(this.nodes ?? []);
      // si le chemin actuel ne correspond plus à rien dans les nouvelles données
      // (ex: le noeud a été supprimé), on le tronque au premier niveau valide
      this.path.set(this.tronquerCheminValide(this.path()));
    }
  }

  private findNodeById(nodes: AssemblageTree[], id: number): AssemblageTree | null {
    for (const n of nodes) {
      if (n.id === id) return n;
      const trouve = this.findNodeById(n.sousAssemblages ?? [], id);
      if (trouve) return trouve;
    }
    return null;
  }

  private tronquerCheminValide(ids: number[]): number[] {
    let niveauCourant = this.nodesSignal();
    const resultat: number[] = [];
    for (const id of ids) {
      const node = niveauCourant.find((n) => n.id === id);
      if (!node) break;
      resultat.push(id);
      niveauCourant = node.sousAssemblages ?? [];
    }
    return resultat;
  }

  // Reconstruit la liste des noeuds parcourus (pour le fil d'Ariane) à partir des IDs
  pathNodes = computed<AssemblageTree[]>(() => {
    const ids = this.path();
    const nodes = this.nodesSignal();
    const resultat: AssemblageTree[] = [];
    let niveauCourant = nodes;
    for (const id of ids) {
      const node = niveauCourant.find((n) => n.id === id);
      if (!node) break;
      resultat.push(node);
      niveauCourant = node.sousAssemblages ?? [];
    }
    return resultat;
  });

  noeudCourant = computed<AssemblageTree | null>(() => {
    const p = this.pathNodes();
    return p.length > 0 ? p[p.length - 1] : null;
  });

  sousAssemblagesCourants = computed<AssemblageTree[]>(() => {
    const courant = this.noeudCourant();
    return courant ? (courant.sousAssemblages ?? []) : this.nodesSignal();
  });

  composantsCourants = computed(() => {
    const courant = this.noeudCourant();
    return courant ? (courant.composants ?? []) : [];
  });

  estFeuille = computed(
    () => this.noeudCourant() !== null && this.sousAssemblagesCourants().length === 0,
  );

  entrerDans(node: AssemblageTree): void {
    this.path.set([...this.path(), node.id]);
    this.showSousZoneForm.set(false);
    this.showComposantForm.set(false);
  }

  allerA(index: number): void {
    if (index < 0) {
      this.path.set([]);
    } else {
      this.path.set(this.path().slice(0, index + 1));
    }
    this.showSousZoneForm.set(false);
    this.showComposantForm.set(false);
  }

  retour(): void {
    this.path.set(this.path().slice(0, -1));
    this.showSousZoneForm.set(false);
    this.showComposantForm.set(false);
  }

  toggleSousZoneForm(): void {
    this.showSousZoneForm.set(!this.showSousZoneForm());
  }

  onSousZoneAjoutee(): void {
    this.showSousZoneForm.set(false);
    this.changed.emit();
  }

  toggleComposantForm(): void {
    this.showComposantForm.set(!this.showComposantForm());
  }

  onComposantAjoute(): void {
    this.showComposantForm.set(false);
    this.changed.emit();
  }

  supprimerAssemblage(node: AssemblageTree, event: MouseEvent): void {
    event.stopPropagation();
    if (this.suppressionEnCours()) return;
    if (!confirm(`Supprimer "${node.nom}" et tout son contenu ?`)) return;

    this.suppressionEnCours.set(node.id);
    this.erreurSuppression.set(null);

    this.assemblageService.supprimer(node.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(null);
        this.changed.emit();
      },
      error: (err) => {
        this.suppressionEnCours.set(null);
        this.erreurSuppression.set(
          err.status === 403
            ? "Vous n'avez pas les droits pour supprimer cette zone."
            : 'Erreur lors de la suppression.',
        );
      },
    });
  }

  supprimerComposant(composant: Composant, event: MouseEvent): void {
    event.stopPropagation();
    if (this.suppressionEnCours()) return;
    if (!confirm(`Supprimer le composant "${composant.nom}" ?`)) return;

    this.suppressionEnCours.set(composant.id);
    this.erreurSuppression.set(null);

    this.composantService.supprimer(composant.id).subscribe({
      next: () => {
        this.suppressionEnCours.set(null);
        this.changed.emit();
      },
      error: (err) => {
        this.suppressionEnCours.set(null);
        this.erreurSuppression.set(
          err.status === 403
            ? "Vous n'avez pas les droits pour supprimer ce composant."
            : 'Erreur lors de la suppression.',
        );
      },
    });
  }

  declencherUploadAssemblage(node: AssemblageTree, event: MouseEvent): void {
    event.stopPropagation();
    this.cibleUpload = { type: 'assemblage', id: node.id };
    this.fileInput.nativeElement.value = '';
    this.fileInput.nativeElement.click();
  }

  declencherUploadComposant(composant: Composant, event: MouseEvent): void {
    event.stopPropagation();
    this.cibleUpload = { type: 'composant', id: composant.id };
    this.fileInput.nativeElement.value = '';
    this.fileInput.nativeElement.click();
  }

  onFichierChoisi(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    const cible = this.cibleUpload;

    if (!file || !cible) return;

    this.erreurUpload.set(null);
    this.uploadEnCours.set(cible);

    const onSuccess = () => {
      this.uploadEnCours.set(null);
      this.cibleUpload = null;
      this.changed.emit();
    };

    const onError = () => {
      this.uploadEnCours.set(null);
      this.cibleUpload = null;
      this.erreurUpload.set("Erreur lors de l'upload de l'image.");
    };

    if (cible.type === 'assemblage') {
      this.assemblageService.uploaderImage(cible.id, file).subscribe({
        next: onSuccess,
        error: onError,
      });
    } else {
      this.composantService.uploaderImage(cible.id, file).subscribe({
        next: onSuccess,
        error: onError,
      });
    }
  }

  estEnUpload(type: 'assemblage' | 'composant', id: number): boolean {
    const u = this.uploadEnCours();
    return u !== null && u.type === type && u.id === id;
  }
}
