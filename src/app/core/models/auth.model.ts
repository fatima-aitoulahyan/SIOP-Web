export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  nom: string;
  prenom: string;
  type: 'ADMINISTRATEUR' | 'CLIENT' | 'RESPONSABLE_MAINTENANCE' | 'TECHNICIEN';
}
