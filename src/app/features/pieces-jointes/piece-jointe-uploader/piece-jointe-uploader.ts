import {
  Component,
  Input,
  Output,
  EventEmitter,
  inject,
  signal,
  OnInit,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { PieceJointeService } from '../services/piece-jointe';
import {
  PieceJointeDTO,
  TypeEntiteJointe,
  TypeFichier,
} from '../../../core/models/piece-jointe.model';
import { forkJoin, of, tap } from 'rxjs';

export interface FichierEnAttente {
  file: File;
  previewUrl: string | null;
  type: TypeFichier;
  description: string;
}

@Component({
  selector: 'app-piece-jointe-uploader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './piece-jointe-uploader.html',
  styleUrl: './piece-jointe-uploader.scss',
})
export class PieceJointeUploaderComponent implements OnInit, OnChanges {
  private pieceJointeService = inject(PieceJointeService);

  // ---- Config ----
  @Input({ required: true }) entiteType!: TypeEntiteJointe;
  @Input() entiteId: number | null = null; // null = mode différé (entité pas encore créée)
  @Input() accept = 'image/*,audio/*';
  @Input() piecesExistantes: PieceJointeDTO[] = [];

  @Output() piecesChanged = new EventEmitter<void>();

  pieces = signal<PieceJointeDTO[]>([]);
  fichiersEnAttente = signal<FichierEnAttente[]>([]);
  uploadEnCours = signal(false);
  suppressionEnCours = signal<number | null>(null);
  erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.pieces.set(this.piecesExistantes ?? []);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['piecesExistantes']) {
      this.pieces.set(this.piecesExistantes ?? []);
    }
  }

  get modeDiffere(): boolean {
    return this.entiteId === null;
  }

  onFichiersSelectionnes(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const fichiers = Array.from(input.files);

    for (const file of fichiers) {
      const type = this.determinerType(file.type);

      if (this.modeDiffere) {
        const previewUrl = type === TypeFichier.IMAGE ? URL.createObjectURL(file) : null;
        this.fichiersEnAttente.update((liste) => [
          ...liste,
          { file, previewUrl, type, description: '' },
        ]);
      } else {
        this.uploaderFichier(file);
      }
    }

    input.value = '';
  }

  private determinerType(mimeType: string): TypeFichier {
    if (mimeType.startsWith('image/')) return TypeFichier.IMAGE;
    if (mimeType.startsWith('audio/')) return TypeFichier.AUDIO;
    if (mimeType === 'application/pdf') return TypeFichier.DOCUMENT;
    return TypeFichier.AUTRE;
  }

  private uploaderFichier(file: File, description?: string): void {
    if (this.entiteId === null) return;

    this.uploadEnCours.set(true);
    this.erreur.set(null);

    this.pieceJointeService.uploader(this.entiteType, this.entiteId, file, description).subscribe({
      next: (res) => {
        this.pieces.update((liste) => [...liste, res.data]);
        this.uploadEnCours.set(false);
        this.piecesChanged.emit();
      },
      error: () => {
        this.erreur.set(`Échec de l'envoi de "${file.name}".`);
        this.uploadEnCours.set(false);
      },
    });
  }

  retirerFichierEnAttente(index: number): void {
    this.fichiersEnAttente.update((liste) => {
      const copie = [...liste];
      const [retire] = copie.splice(index, 1);
      if (retire.previewUrl) URL.revokeObjectURL(retire.previewUrl);
      return copie;
    });
  }

  supprimerPieceExistante(piece: PieceJointeDTO): void {
    const confirmation = confirm(`Supprimer "${piece.nomFichier}" ?`);
    if (!confirmation) return;

    this.suppressionEnCours.set(piece.id);
    this.pieceJointeService.supprimer(piece.id).subscribe({
      next: () => {
        this.pieces.update((liste) => liste.filter((p) => p.id !== piece.id));
        this.suppressionEnCours.set(null);
        this.piecesChanged.emit();
      },
      error: () => {
        this.erreur.set('Échec de la suppression.');
        this.suppressionEnCours.set(null);
      },
    });
  }
  uploaderFichiersEnAttente(entiteId: number) {
    const fichiers = this.fichiersEnAttente();
    if (fichiers.length === 0) {
      return of([]); // On retourne un tableau vide observable si pas de fichiers
    }

    this.uploadEnCours.set(true);
    this.erreur.set(null);

    const requetesUpload = fichiers.map((f) =>
      this.pieceJointeService.uploader(
        this.entiteType,
        entiteId,
        f.file,
        f.description || undefined,
      ),
    );

    return forkJoin(requetesUpload).pipe(
      tap({
        next: (responses: any[]) => {
          responses.forEach((res) => {
            this.pieces.update((liste) => [...liste, res.data]);
          });
          this.fichiersEnAttente.set([]);
          this.uploadEnCours.set(false);
          this.piecesChanged.emit();
        },
        error: () => {
          this.erreur.set(`Échec lors de l'envoi de certains fichiers.`);
          this.uploadEnCours.set(false);
        },
      }),
    );
  }

  get TypeFichier() {
    return TypeFichier;
  }
}
