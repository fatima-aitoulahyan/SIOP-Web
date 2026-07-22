import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  DemandeMaintenanceCreateDTO,
  DemandeMaintenanceDTO,
} from '../../../core/models/demande-maintenance.model';

@Injectable({ providedIn: 'root' })
export class DemandeMaintenanceService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/demandes-maintenance`;

  // NB : le DemandeMaintenanceController renvoie les DTO directement
  // (pas d'enveloppe ApiResponse), contrairement à d'autres modules.

  creer(dto: DemandeMaintenanceCreateDTO): Observable<DemandeMaintenanceDTO> {
    return this.http.post<DemandeMaintenanceDTO>(this.baseUrl, dto);
  }

  listerMesDemandes(): Observable<DemandeMaintenanceDTO[]> {
    return this.http.get<DemandeMaintenanceDTO[]>(`${this.baseUrl}/mes-demandes`);
  }

  getDetail(id: number): Observable<DemandeMaintenanceDTO> {
    return this.http.get<DemandeMaintenanceDTO>(`${this.baseUrl}/${id}`);
  }

  annuler(id: number): Observable<DemandeMaintenanceDTO> {
    return this.http.patch<DemandeMaintenanceDTO>(`${this.baseUrl}/${id}/annuler`, {});
  }
}
