import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { ParcDTO, ParcCreateDTO, ParcDetailDTO } from '../../../core/models/parc.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ParcService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/parcs`;

  listerTous(): Observable<ParcDTO[]> {
    return this.http.get<ApiResponse<ParcDTO[]>>(this.baseUrl).pipe(map((res) => res.data));
  }

  getById(id: number): Observable<ParcDetailDTO> {
    return this.http.get<ApiResponse<ParcDetailDTO>>(`${this.baseUrl}/${id}`).pipe(map((res) => res.data));
  }

  creer(dto: ParcCreateDTO): Observable<ParcDTO> {
    return this.http.post<ApiResponse<ParcDTO>>(this.baseUrl, dto).pipe(map((res) => res.data));
  }

  modifier(id: number, dto: ParcCreateDTO): Observable<ParcDTO> {
    return this.http.put<ApiResponse<ParcDTO>>(`${this.baseUrl}/${id}`, dto).pipe(map((res) => res.data));
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }

  affecterTechnicien(parcId: number, technicienId: number): Observable<ParcDetailDTO> {
  return this.http.post<ApiResponse<ParcDetailDTO>>(`${this.baseUrl}/${parcId}/techniciens/${technicienId}`, {}).pipe(map((res) => res.data));
}

  retirerTechnicien(parcId: number, technicienId: number): Observable<ParcDetailDTO> {
  return this.http.delete<ApiResponse<ParcDetailDTO>>(`${this.baseUrl}/${parcId}/techniciens/${technicienId}`).pipe(map((res) => res.data)); 
 }
}