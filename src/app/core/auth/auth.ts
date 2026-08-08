import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { LoginRequest, LoginResponse } from '../models/auth.model';
import { ActivationCompteRequest } from '../models/utilisateur.model';

interface JwtPayload {
  sub: string;
  exp: number;
  [key: string]: any;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'siop_token';
  private typeKey = 'siop_type';

  currentRole = signal<string | null>(this.getStoredRole());
  isAuthenticated = computed(() => !!this.currentRole());

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

  private getStoredRole(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<JwtPayload>(token);
      if (decoded.exp * 1000 < Date.now()) {
        this.logout();
        return null;
      }
      return localStorage.getItem(this.typeKey);
    } catch {
      return null;
    }
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
