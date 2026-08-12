import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { environment } from '../../../../environments/environment';
import {
  EvaluationAscenseurDTO,
  EvaluationAscenseurSoumissionDTO,
  EvaluationAscenseurValidationDTO,
} from '../../../core/models/evaluation-ascenseur.model';

@Injectable({ providedIn: 'root' })
export class EvaluationAscenseurService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/evaluations-ascenseur`;

  creerBrouillon(bonTravailId: number): Observable<EvaluationAscenseurDTO> {
    return this.http
      .post<ApiResponse<EvaluationAscenseurDTO>>(`${this.baseUrl}/bon-travail/${bonTravailId}`, {})
      .pipe(map((res) => res.data));
  }

  soumettre(
    evaluationId: number,
    dto: EvaluationAscenseurSoumissionDTO,
  ): Observable<EvaluationAscenseurDTO> {
    return this.http
      .put<ApiResponse<EvaluationAscenseurDTO>>(`${this.baseUrl}/${evaluationId}/soumettre`, dto)
      .pipe(map((res) => res.data));
  }

  valider(
    evaluationId: number,
    dto: EvaluationAscenseurValidationDTO,
  ): Observable<EvaluationAscenseurDTO> {
    return this.http
      .put<ApiResponse<EvaluationAscenseurDTO>>(`${this.baseUrl}/${evaluationId}/valider`, dto)
      .pipe(map((res) => res.data));
  }

  getById(id: number): Observable<EvaluationAscenseurDTO> {
    return this.http
      .get<ApiResponse<EvaluationAscenseurDTO>>(`${this.baseUrl}/${id}`)
      .pipe(map((res) => res.data));
  }

  getByBonTravailId(bonTravailId: number): Observable<EvaluationAscenseurDTO> {
    return this.http
      .get<ApiResponse<EvaluationAscenseurDTO>>(`${this.baseUrl}/bon-travail/${bonTravailId}`)
      .pipe(map((res) => res.data));
  }

  getEnAttenteValidation(): Observable<EvaluationAscenseurDTO[]> {
    return this.http
      .get<ApiResponse<EvaluationAscenseurDTO[]>>(`${this.baseUrl}/en-attente`)
      .pipe(map((res) => res.data));
  }

  mesEvaluations(): Observable<EvaluationAscenseurDTO[]> {
    return this.http
      .get<ApiResponse<EvaluationAscenseurDTO[]>>(`${this.baseUrl}/mes-evaluations`)
      .pipe(map((res) => res.data));
  }
}
