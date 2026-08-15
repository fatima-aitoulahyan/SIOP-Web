import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MaintenanceService } from '../../services/maintenance.service';
import { AuthService } from '../../../../core/auth/auth';
import { AscenseurService } from '../../../ascenseurs/services/ascenseur.service';
import { environment } from '../../../../../environments/environment';
import {
  PrioriteDemande,
} from '../../../../core/models/maintenance.model';
import { AscenseurDTO } from '../../../../core/models/ascenseur.model';
import { TypeDemande, TYPE_DEMANDE_LABELS } from '../../../../core/models/demande-maintenance.model';
import { CreerDemandeEvaluationComponent } from '../creer-demande-evaluation/creer-demande-evaluation';

@Component({
  selector: 'app-creer-demande-mantenance',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './creer-demande-mantenance.html',
  styleUrls: ['./creer-demande-mantenance.scss'],
})
export class CreerDemandeMantenanceComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private maintenanceService = inject(MaintenanceService);
  private ascenseurService = inject(AscenseurService);
  private authService = inject(AuthService);
  private http = inject(HttpClient);
  private router = inject(Router);

  envoiEnCours = signal(false);
  erreur = signal<string | null>(null);
  photosSelectionnees = signal<File[]>([]);
  photosErreur = signal<string | null>(null);
  photoUrls = signal<Map<number, string>>(new Map());
  envoiPhotos = signal(false);

  enregistrementEnCours = signal(false);
  audioBlob = signal<Blob | null>(null);
  audioUrlSignal = signal<string>('');
  dureeEnregistrement = signal(0);
  audioErreur = signal<string | null>(null);
  traitementAudio = signal(false);

  ascenseurs = signal<AscenseurDTO[]>([]);
  types = Object.values(TypeDemande).filter((t) => t !== TypeDemande.EVALUATION);
  priorites = Object.values(PrioriteDemande);
  readonly TypeDemandeLabels = TYPE_DEMANDE_LABELS;

  private mediaRecorder: MediaRecorder | null = null;
  private timerInterval: ReturnType<typeof setInterval> | null = null;
  private audioStream: MediaStream | null = null;

  form = this.fb.nonNullable.group({
    ascenseurId: [null as number | null, Validators.required],
    typeDemande: [null as TypeDemande | null, Validators.required],
    priorite: [PrioriteDemande.NORMALE],
    description: ['', [Validators.required, Validators.minLength(10)]],
    dateSouhaitee: [''],
  });

  ngOnInit(): void {
    this.chargerAscenseurs();
  }

  ngOnDestroy(): void {
    this.arreterEnregistrement();
    this.audioStream?.getTracks().forEach((t) => t.stop());
    const audioUrl = this.audioUrlSignal();
    if (audioUrl) URL.revokeObjectURL(audioUrl);
    this.photoUrls().forEach((url) => URL.revokeObjectURL(url));
  }

  private chargerAscenseurs(): void {
    this.authService.monProfil().subscribe({
      next: (profil) => {
        this.ascenseurService.listerParClient(profil.id).subscribe({
          next: (res) => this.ascenseurs.set(res.data),
          error: () => this.erreur.set('Impossible de charger vos ascenseurs.'),
        });
      },
      error: () => this.erreur.set('Impossible de récupérer votre profil.'),
    });
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;
    const newFiles = Array.from(input.files);
    const combined = [...this.photosSelectionnees(), ...newFiles];

    if (combined.length > 5) {
      this.photosErreur.set(
        `Maximum 5 photos autorisées. ${combined.length - 5} photo(s) non ajoutée(s).`,
      );
    } else {
      this.photosErreur.set(null);
    }

    const total = combined.slice(0, 5);
    this.photosSelectionnees.set(total);

    const oldUrls = this.photoUrls();
    oldUrls.forEach((url) => URL.revokeObjectURL(url));

    const urls = new Map<number, string>();
    total.forEach((f, i) => urls.set(i, URL.createObjectURL(f)));
    this.photoUrls.set(urls);
  }

  async startRecording(): Promise<void> {
    try {
      this.audioErreur.set(null);
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.audioStream = stream;

      const mimeType = MediaRecorder.isTypeSupported('audio/webm') ? 'audio/webm' : 'audio/ogg';

      this.mediaRecorder = new MediaRecorder(stream, { mimeType });

      const recordedMimeType = mimeType;
      const chunks: Blob[] = [];

      this.mediaRecorder.ondataavailable = (e) => {
        if (e.data.size > 0) chunks.push(e.data);
      };

      this.mediaRecorder.onstop = () => {
        const blob = new Blob(chunks, { type: recordedMimeType });
        const url = URL.createObjectURL(blob);
        this.audioStream?.getTracks().forEach((t) => t.stop());
        setTimeout(() => {
          this.audioBlob.set(blob);
          this.audioUrlSignal.set(url);
          this.traitementAudio.set(false);
        });
      };

      this.mediaRecorder.start();
      this.enregistrementEnCours.set(true);
      this.dureeEnregistrement.set(0);

      this.timerInterval = setInterval(() => {
        this.dureeEnregistrement.update((v) => v + 1);
      }, 1000);
    } catch {
      this.audioErreur.set("Impossible d'accéder au microphone. Vérifiez les permissions.");
    }
  }

  stopRecording(): void {
    this.arreterEnregistrement();
  }

  private arreterEnregistrement(): void {
    this.traitementAudio.set(true);
    this.mediaRecorder?.stop();
    this.enregistrementEnCours.set(false);
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  retirerAudio(): void {
    const url = this.audioUrlSignal();
    if (url) URL.revokeObjectURL(url);
    this.audioUrlSignal.set('');
    this.audioBlob.set(null);
    this.audioErreur.set(null);
  }

  formatDuree(secondes: number): string {
    const m = Math.floor(secondes / 60);
    const s = secondes % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  soumettre(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.envoiEnCours.set(true);
    this.erreur.set(null);
    const raw = this.form.getRawValue();

    this.maintenanceService
      .creer({
        ascenseurId: raw.ascenseurId!,
        typeDemande: raw.typeDemande!,
        priorite: raw.priorite!,
        description: raw.description,
        dateSouhaitee: raw.dateSouhaitee || null,
      })
      .subscribe({
        next: (demande) => {
          const fichiers: File[] = [...this.photosSelectionnees()];
          const audio = this.audioBlob();
          if (audio) {
            const baseType = audio.type.split(';')[0];
            const ext =
              baseType === 'audio/webm' ? 'webm' : baseType === 'audio/ogg' ? 'ogg' : 'mp4';
            fichiers.push(new File([audio], `enregistrement.${ext}`, { type: baseType }));
          }

          if (fichiers.length === 0) {
            this.router.navigate(['/client/demandes']);
            return;
          }

          this.envoiPhotos.set(true);
          let uploaded = 0;
          fichiers.forEach((fichier) => {
            const fd = new FormData();
            fd.append('fichier', fichier);
            fd.append('entiteType', 'DEMANDE_MAINTENANCE');
            fd.append('entiteId', String(demande.id));

            this.http.post(`${environment.apiUrl}/pieces-jointes`, fd).subscribe({
              next: () => {
                uploaded++;
                if (uploaded === fichiers.length) {
                  this.router.navigate(['/client/demandes']);
                }
              },
              error: () => {
                this.erreur.set("Erreur lors de l'upload d'un fichier.");
                this.envoiEnCours.set(false);
                this.envoiPhotos.set(false);
              },
            });
          });
        },
        error: () => {
          this.erreur.set('La création de la demande a échoué.');
          this.envoiEnCours.set(false);
        },
      });
  }
}
