import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { VilleDTO } from '../../../core/models/ville.model';
import { environment } from '../../../../environments/environment';
@Injectable({ providedIn: 'root' })
export class VilleService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}`;

  listerTous(): Observable<VilleDTO[]> {
    return this.http
      .get<ApiResponse<VilleDTO[]>>(`${this.baseUrl}/villes` )
      .pipe(map((res) => res.data!));
  }

  creer(dto: { nom: string; region?: string; codePostal?: string }): Observable<VilleDTO> {
    return this.http
      .post<ApiResponse<VilleDTO>>(`${this.baseUrl}/referentiel/villes` , dto)
      .pipe(map((res) => res.data!));
  }
}
