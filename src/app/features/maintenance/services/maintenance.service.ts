import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import {
  DemandeMaintenanceDTO,
  DemandeMaintenanceCreateDTO,
  RejetDemandeDTO,
  StatutDemande,
} from '../../../core/models/maintenance.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/demandes-maintenance`;

  // ── Client ──
  creer(dto: DemandeMaintenanceCreateDTO): Observable<DemandeMaintenanceDTO> {
    return this.http.post<ApiResponse<DemandeMaintenanceDTO>>(this.baseUrl, dto).pipe(map((res) => res.data));
  }

  mesDemandes(): Observable<DemandeMaintenanceDTO[]> {
    return this.http.get<ApiResponse<DemandeMaintenanceDTO[]>>(`${this.baseUrl}/mes-demandes`).pipe(map((res) => res.data));
  }

  getDetail(id: number): Observable<DemandeMaintenanceDTO> {
    return this.http.get<ApiResponse<DemandeMaintenanceDTO>>(`${this.baseUrl}/${id}`).pipe(map((res) => res.data));
  }

  annuler(id: number): Observable<DemandeMaintenanceDTO> {
    return this.http.patch<ApiResponse<DemandeMaintenanceDTO>>(`${this.baseUrl}/${id}/annuler`, {}).pipe(map((res) => res.data));
  }

  // ── Responsable ──
  demandesEnAttente(): Observable<DemandeMaintenanceDTO[]> {
    return this.http.get<ApiResponse<DemandeMaintenanceDTO[]>>(`${this.baseUrl}/en-attente`).pipe(map((res) => res.data));
  }

  toutesDemandes(statut?: StatutDemande | null): Observable<DemandeMaintenanceDTO[]> {
    let params = new HttpParams();
    if (statut) params = params.set('statut', statut);
    return this.http.get<ApiResponse<DemandeMaintenanceDTO[]>>(`${this.baseUrl}/toutes`, { params }).pipe(map((res) => res.data));
  }

  getDetailPourResponsable(id: number): Observable<DemandeMaintenanceDTO> {
    return this.http.get<ApiResponse<DemandeMaintenanceDTO>>(`${this.baseUrl}/gestion/${id}`).pipe(map((res) => res.data));
  }

  rejeter(id: number, dto: RejetDemandeDTO): Observable<DemandeMaintenanceDTO> {
    return this.http.patch<ApiResponse<DemandeMaintenanceDTO>>(`${this.baseUrl}/${id}/rejeter`, dto).pipe(map((res) => res.data));
  }
}