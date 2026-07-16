import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../core/models/api-response.model';
import {
  Composant,
  ComposantCreateDTO,
  ComposantUpdateDTO,
} from '../../../core/models/composant.model';

@Injectable({ providedIn: 'root' })
export class ComposantService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/composants`;

  listerParAssemblage(assemblageId: number) {
    return this.http.get<ApiResponse<Composant[]>>(`${this.baseUrl}/assemblage/${assemblageId}`);
  }

  creer(dto: ComposantCreateDTO) {
    return this.http.post<ApiResponse<Composant>>(this.baseUrl, dto);
  }

  modifier(id: number, dto: ComposantUpdateDTO) {
    return this.http.put<ApiResponse<Composant>>(`${this.baseUrl}/${id}`, dto);
  }

  supprimer(id: number) {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
