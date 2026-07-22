import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../core/models/api-response.model';
import {
  Assemblage,
  AssemblageCreateDTO,
  AssemblageUpdateDTO,
  AssemblageTree,
} from '../../../core/models/assemblage.model';

@Injectable({ providedIn: 'root' })
export class AssemblageService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/assemblages`;

  getById(id: number) {
    return this.http.get<ApiResponse<Assemblage>>(`${this.baseUrl}/${id}`);
  }

  listerParAscenseur(ascenseurId: number) {
    return this.http.get<ApiResponse<Assemblage[]>>(`${this.baseUrl}/ascenseur/${ascenseurId}`);
  }

  arbreParAscenseur(ascenseurId: number) {
    return this.http.get<ApiResponse<AssemblageTree[]>>(
      `${this.baseUrl}/ascenseur/${ascenseurId}/arbre`,
    );
  }

  creer(dto: AssemblageCreateDTO) {
    return this.http.post<ApiResponse<Assemblage>>(this.baseUrl, dto);
  }

  modifier(id: number, dto: AssemblageUpdateDTO) {
    return this.http.put<ApiResponse<Assemblage>>(`${this.baseUrl}/${id}`, dto);
  }

  supprimer(id: number) {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
