import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { TechnicienResumeDTO } from '../models/technicien.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TechnicienService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/techniciens`;

  lister(): Observable<TechnicienResumeDTO[]> {
    return this.http.get<ApiResponse<TechnicienResumeDTO[]>>(this.baseUrl).pipe(map((res) => res.data));
  }

  listerParParc(parcId: number): Observable<TechnicienResumeDTO[]> {
    return this.http
      .get<ApiResponse<TechnicienResumeDTO[]>>(`${this.baseUrl}/parc/${parcId}`)
      .pipe(map((res) => res.data));
  }
}