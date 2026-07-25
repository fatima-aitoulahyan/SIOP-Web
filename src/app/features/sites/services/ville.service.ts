import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { VilleDTO } from '../../../core/models/ville.model';

@Injectable({ providedIn: 'root' })
export class VilleService {
  private http = inject(HttpClient);
  private baseUrl = '/api/villes';

  listerTous(): Observable<VilleDTO[]> {
    return this.http
      .get<ApiResponse<VilleDTO[]>>('/api/referentiel/villes')
      .pipe(map((res) => res.data!));
  }

  creer(dto: { nom: string }): Observable<VilleDTO> {
    return this.http
      .post<ApiResponse<VilleDTO>>('/api/referentiel/villes', dto)
      .pipe(map((res) => res.data!));
  }
}
