import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../core/models/api-response.model';
import {
  UtilisateurRequestDTO,
  UtilisateurResponseDTO,
} from '../../../core/models/utilisateur.model';

@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/utilisateurs`;

  creer(dto: UtilisateurRequestDTO): Observable<UtilisateurResponseDTO> {
    return this.http
      .post<ApiResponse<UtilisateurResponseDTO>>(this.baseUrl, dto)
      .pipe(map((res) => res.data));
  }

  getAll(): Observable<UtilisateurResponseDTO[]> {
    return this.http
      .get<ApiResponse<UtilisateurResponseDTO[]>>(this.baseUrl)
      .pipe(map((res) => res.data));
  }

  getById(id: number): Observable<UtilisateurResponseDTO> {
    return this.http
      .get<ApiResponse<UtilisateurResponseDTO>>(`${this.baseUrl}/${id}`)
      .pipe(map((res) => res.data));
  }

  modifier(id: number, dto: UtilisateurRequestDTO): Observable<UtilisateurResponseDTO> {
    return this.http
      .put<ApiResponse<UtilisateurResponseDTO>>(`${this.baseUrl}/${id}`, dto)
      .pipe(map((res) => res.data));
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }

  desactiver(id: number): Observable<void> {
    return this.http
      .patch<ApiResponse<void>>(`${this.baseUrl}/${id}/desactiver`, {})
      .pipe(map(() => undefined));
  }
  getClients(): Observable<UtilisateurResponseDTO[]> {
    return this.http
      .get<ApiResponse<UtilisateurResponseDTO[]>>(`${this.baseUrl}/clients`)
      .pipe(map((res) => res.data));
  }
}
