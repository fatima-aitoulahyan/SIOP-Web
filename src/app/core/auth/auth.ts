import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { LoginRequest, LoginResponse } from '../models/auth.model';
import { ActivationCompteRequest } from '../models/utilisateur.model';
import { ProfilDTO } from '../models/utilisateur.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'siop_token';
  private typeKey = 'siop_type';

  currentRole = signal<string | null>(localStorage.getItem(this.typeKey));
  isAuthenticated = computed(() => !!this.currentRole());
  authReady = signal(false);

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.baseUrl}/login`, credentials).pipe(
      tap((res) => {
        localStorage.setItem(this.tokenKey, res.data.token);
        localStorage.setItem(this.typeKey, res.data.type);
        this.currentRole.set(res.data.type);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.typeKey);
    this.currentRole.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  me(): Observable<ProfilDTO> {
    return this.http.get<ApiResponse<ProfilDTO>>(`${this.baseUrl}/me`).pipe(
      tap((res) => {
        this.currentRole.set(res.data.role);
        localStorage.setItem(this.typeKey, res.data.role);
      }),
      map((res) => res.data),
    );
  }

  // Ne modifie pas currentRole/localStorage : pour les besoins ponctuels (formulaires),
  // à ne pas confondre avec me() qui resynchronise la session au démarrage de l'app.
  monProfil(): Observable<ProfilDTO> {
    return this.http.get<ApiResponse<ProfilDTO>>(`${this.baseUrl}/me`).pipe(map((res) => res.data));
  }

  activerCompte(dto: ActivationCompteRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.baseUrl}/activer-compte`, dto);
  }
  motDePasseOublie(email: string) {
    return this.http.post<any>(`${this.baseUrl}/mot-de-passe-oublie`, { email });
  }

  reinitialiserMotDePasse(token: string, nouveauMotDePasse: string) {
    return this.http.post<any>(`${this.baseUrl}/reinitialiser-mot-de-passe`, {
      token,
      nouveauMotDePasse,
    });
  }
}
