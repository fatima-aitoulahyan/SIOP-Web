import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { SiteCreateDTO, SiteDTO, SiteUpdateDTO } from '../../../core/models/site.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SiteService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/sites`;

  creer(dto: SiteCreateDTO): Observable<SiteDTO> {
    return this.http.post<ApiResponse<SiteDTO>>(this.baseUrl, dto).pipe(map((res) => res.data));
  }

  getById(id: number): Observable<SiteDTO> {
    return this.http
      .get<ApiResponse<SiteDTO>>(`${this.baseUrl}/${id}`)
      .pipe(map((res) => res.data));
  }

  listerTous(): Observable<SiteDTO[]> {
    return this.http.get<ApiResponse<SiteDTO[]>>(this.baseUrl).pipe(map((res) => res.data));
  }

  listerParVille(villeId: number): Observable<SiteDTO[]> {
    return this.http
      .get<ApiResponse<SiteDTO[]>>(`${this.baseUrl}/ville/${villeId}`)
      .pipe(map((res) => res.data));
  }

  listerParClient(clientId: number): Observable<SiteDTO[]> {
    return this.http
      .get<ApiResponse<SiteDTO[]>>(`${this.baseUrl}/client/${clientId}`)
      .pipe(map((res) => res.data));
  }

  modifier(id: number, dto: SiteUpdateDTO): Observable<SiteDTO> {
    return this.http
      .put<ApiResponse<SiteDTO>>(`${this.baseUrl}/${id}`, dto)
      .pipe(map((res) => res.data));
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }

  mesSites(): Observable<SiteDTO[]> {
    return this.http.get<ApiResponse<SiteDTO[]>>(`${this.baseUrl}/mes-sites`).pipe(map((res) => res.data));
  }

  sitesDeMonParc(): Observable<SiteDTO[]> {
     return this.http.get<ApiResponse<SiteDTO[]>>(`${this.baseUrl}/mon-parc`).pipe(map((res) => res.data));
  }
}
