import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { environment } from '../../../../environments/environment';
import {
  BonTravailCreateDTO,
  BonTravailDTO,
  BonTravailResumeDTO,
  ChecklistMaintenanceDTO,
  ClotureBonTravailDTO,
  ClotureChecklistDTO,
  CommentaireDTO,
  ConflitTechnicienDTO,
  ItemCheckListUpdateDTO,
} from '../../../core/models/bon-travail.model';
import { TechnicienResumeDTO } from '../../../core/models/technicien.model';

@Injectable({ providedIn: 'root' })
export class BonTravailService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/bons-travail`;
  private readonly checklistUrl = `${environment.apiUrl}/checklists`;

  // ── Responsable ──
  lister(): Observable<BonTravailResumeDTO[]> {
    return this.http
      .get<ApiResponse<BonTravailResumeDTO[]>>(this.baseUrl)
      .pipe(map((res) => res.data));
  }

  getDetail(id: number): Observable<BonTravailDTO> {
    return this.http
      .get<ApiResponse<BonTravailDTO>>(`${this.baseUrl}/${id}`)
      .pipe(map((res) => res.data));
  }

  creer(dto: BonTravailCreateDTO): Observable<BonTravailDTO> {
    return this.http
      .post<ApiResponse<BonTravailDTO>>(this.baseUrl, dto)
      .pipe(map((res) => res.data));
  }

  annuler(id: number): Observable<BonTravailDTO> {
    return this.http
      .patch<ApiResponse<BonTravailDTO>>(`${this.baseUrl}/${id}/annuler`, {})
      .pipe(map((res) => res.data));
  }

  verifierDisponibilite(
    technicienIds: number[],
    debut: string,
    dureeMinutes: number,
  ): Observable<ConflitTechnicienDTO[]> {
    let params = new HttpParams().set('debut', debut).set('dureeMinutes', String(dureeMinutes));
    technicienIds.forEach((id) => {
      params = params.append('technicienIds', String(id));
    });
    return this.http
      .get<ApiResponse<ConflitTechnicienDTO[]>>(`${this.baseUrl}/verifier-disponibilite`, {
        params,
      })
      .pipe(map((res) => res.data));
  }

  techniciensDisponibles(
    ascenseurId: number,
    debut: string,
    dureeMinutes: number,
  ): Observable<TechnicienResumeDTO[]> {
    let params = new HttpParams()
      .set('ascenseurId', String(ascenseurId))
      .set('debut', debut)
      .set('dureeMinutes', String(dureeMinutes));
    return this.http
      .get<ApiResponse<TechnicienResumeDTO[]>>(`${this.baseUrl}/techniciens-disponibles`, {
        params,
      })
      .pipe(map((res) => res.data));
  }

  // ── Technicien ──
  mesInterventions(): Observable<BonTravailResumeDTO[]> {
    return this.http
      .get<ApiResponse<BonTravailResumeDTO[]>>(`${this.baseUrl}/mes-interventions`)
      .pipe(map((res) => res.data));
  }

  getIntervention(id: number): Observable<BonTravailDTO> {
    return this.http
      .get<ApiResponse<BonTravailDTO>>(`${this.baseUrl}/mes-interventions/${id}`)
      .pipe(map((res) => res.data));
  }

  demarrerIntervention(id: number): Observable<BonTravailDTO> {
    return this.http
      .patch<ApiResponse<BonTravailDTO>>(`${this.baseUrl}/${id}/demarrer`, {})
      .pipe(map((res) => res.data));
  }

  terminerIntervention(id: number, dto: ClotureBonTravailDTO): Observable<BonTravailDTO> {
    return this.http
      .patch<ApiResponse<BonTravailDTO>>(`${this.baseUrl}/${id}/terminer`, dto)
      .pipe(map((res) => res.data));
  }

  // ── Checklists ──
  checklistParBonTravail(bonTravailId: number): Observable<ChecklistMaintenanceDTO> {
    return this.http
      .get<ApiResponse<ChecklistMaintenanceDTO>>(
        `${this.checklistUrl}/par-bon-travail/${bonTravailId}`,
      )
      .pipe(map((res) => res.data));
  }

  demarrerChecklist(id: number): Observable<ChecklistMaintenanceDTO> {
    return this.http
      .patch<ApiResponse<ChecklistMaintenanceDTO>>(`${this.checklistUrl}/${id}/demarrer`, {})
      .pipe(map((res) => res.data));
  }

  cocherItem(itemId: number, dto: ItemCheckListUpdateDTO): Observable<ChecklistMaintenanceDTO> {
    return this.http
      .patch<ApiResponse<ChecklistMaintenanceDTO>>(`${this.checklistUrl}/items/${itemId}`, dto)
      .pipe(map((res) => res.data));
  }

  cloturerChecklist(id: number, dto: ClotureChecklistDTO): Observable<ChecklistMaintenanceDTO> {
    return this.http
      .patch<ApiResponse<ChecklistMaintenanceDTO>>(`${this.checklistUrl}/${id}/cloturer`, dto)
      .pipe(map((res) => res.data));
  }

  listerCommentaires(btId: number) {
    return this.http.get<CommentaireDTO[]>(
      `${environment.apiUrl}/bons-travail/${btId}/commentaires`,
    );
  }

  ajouterCommentaire(btId: number, contenu: string) {
    return this.http.post<CommentaireDTO>(
      `${environment.apiUrl}/bons-travail/${btId}/commentaires`,
      { contenu },
    );
  }
  supprimerCommentaire(btId: number, commentaireId: number) {
    return this.http.delete<void>(
      `${environment.apiUrl}/bons-travail/${btId}/commentaires/${commentaireId}`,
    );
  }
}
