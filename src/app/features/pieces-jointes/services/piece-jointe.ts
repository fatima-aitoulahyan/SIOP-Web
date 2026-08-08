import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { PieceJointeDTO, TypeEntiteJointe } from '../../../core/models/piece-jointe.model';

@Injectable({ providedIn: 'root' })
export class PieceJointeService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/pieces-jointes';

  /**
   * ⚠️ Suppose un endpoint GET /api/pieces-jointes?entiteType=...&entiteId=...
   * côté backend. Adapte l'URL/les query params si ton contrôleur Spring
   * expose ça différemment (ex: /api/pieces-jointes/ascenseur/{id}).
   */
  lister(
    entiteType: TypeEntiteJointe,
    entiteId: number,
  ): Observable<ApiResponse<PieceJointeDTO[]>> {
    const params = new HttpParams()
      .set('entiteType', entiteType)
      .set('entiteId', entiteId.toString());

    return this.http.get<ApiResponse<PieceJointeDTO[]>>(this.baseUrl, { params });
  }

  uploader(
    entiteType: TypeEntiteJointe,
    entiteId: number,
    fichier: File,
    description?: string,
  ): Observable<ApiResponse<PieceJointeDTO>> {
    const formData = new FormData();
    formData.append('entiteType', entiteType);
    formData.append('entiteId', entiteId.toString());
    formData.append('fichier', fichier);
    if (description) formData.append('description', description);

    return this.http.post<ApiResponse<PieceJointeDTO>>(this.baseUrl, formData);
  }

  supprimer(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
