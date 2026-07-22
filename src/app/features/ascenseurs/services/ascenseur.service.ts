import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../core/models/api-response.model';
import {
  AscenseurDTO,
  AscenseurCreateDTO,
  AscenseurUpdateDTO,
} from '../../../core/models/ascenseur.model';

@Injectable({ providedIn: 'root' })
export class AscenseurService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/ascenseurs`;

  listerTous(): Observable<ApiResponse<AscenseurDTO[]>> {
    return this.http.get<ApiResponse<AscenseurDTO[]>>(this.baseUrl);
  }

  getById(id: number): Observable<ApiResponse<AscenseurDTO>> {
    return this.http.get<ApiResponse<AscenseurDTO>>(`${this.baseUrl}/${id}`);
  }

  listerParClient(clientId: number): Observable<ApiResponse<AscenseurDTO[]>> {
    return this.http.get<ApiResponse<AscenseurDTO[]>>(`${this.baseUrl}/client/${clientId}`);
  }

  mesAscenseurs(): Observable<ApiResponse<AscenseurDTO[]>> {
    return this.http.get<ApiResponse<AscenseurDTO[]>>(`${this.baseUrl}/mes-ascenseurs`);
  }

  creer(dto: AscenseurCreateDTO): Observable<ApiResponse<AscenseurDTO>> {
    return this.http.post<ApiResponse<AscenseurDTO>>(this.baseUrl, dto);
  }

  modifier(id: number, dto: AscenseurUpdateDTO): Observable<ApiResponse<AscenseurDTO>> {
    return this.http.put<ApiResponse<AscenseurDTO>>(`${this.baseUrl}/${id}`, dto);
  }

  supprimer(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
