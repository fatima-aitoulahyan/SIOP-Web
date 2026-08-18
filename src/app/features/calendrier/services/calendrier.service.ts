import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import {
  CalendrierEventDTO,
  EvenementRequestDTO,
} from '../models/calendrier-event.model';
import { environment } from '../../../../environments/environment';

interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
}

@Injectable({ providedIn: 'root' })
export class CalendrierService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/calendrier`;
  private evenementsUrl = `${environment.apiUrl}/evenements`;

  getEvenementsCalendrier(
    debut: string,
    fin: string,
    technicienId?: number | null,
  ): Observable<CalendrierEventDTO[]> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    if (technicienId != null) {
      params = params.set('technicienId', technicienId.toString());
    }

    return this.http
      .get<ApiResponse<CalendrierEventDTO[]>>(this.baseUrl, { params })
      .pipe(map((res) => res.data ?? []));
  }

  creerEvenement(dto: EvenementRequestDTO): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(this.evenementsUrl, dto);
  }

  modifierEvenement(id: number, dto: EvenementRequestDTO): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.evenementsUrl}/${id}`, dto);
  }

  supprimerEvenement(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.evenementsUrl}/${id}`);
  }
  getTechniciens(): Observable<any[]> {
    return this.http
      .get<ApiResponse<any[]>>(`${environment.apiUrl}/techniciens`)
      .pipe(map((res) => res.data ?? []));
  }
}
