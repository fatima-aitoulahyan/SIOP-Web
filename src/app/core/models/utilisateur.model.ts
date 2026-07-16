export enum TypeUtilisateur {
  CLIENT = 'CLIENT',
  TECHNICIEN = 'TECHNICIEN',
  RESPONSABLE_MAINTENANCE = 'RESPONSABLE_MAINTENANCE',
  RESPONSABLE_ACHATS = 'RESPONSABLE_ACHATS',
  ADMINISTRATEUR = 'ADMINISTRATEUR',
}

export const TYPE_UTILISATEUR_LABELS: Record<TypeUtilisateur, string> = {
  [TypeUtilisateur.CLIENT]: 'Client',
  [TypeUtilisateur.TECHNICIEN]: 'Technicien',
  [TypeUtilisateur.RESPONSABLE_MAINTENANCE]: 'Responsable Maintenance',
  [TypeUtilisateur.RESPONSABLE_ACHATS]: 'Responsable Achats',
  [TypeUtilisateur.ADMINISTRATEUR]: 'Administrateur',
};

export interface UtilisateurRequestDTO {
  email: string;
  telephone?: string;
  motDePasse: string;
  nom: string;
  prenom: string;
  nomEntreprise?: string;
  type: TypeUtilisateur;
  adresse?: string;
  specialite?: string;
}

export interface UtilisateurResponseDTO {
  id: number;
  email: string;
  telephone?: string;
  nom: string;
  prenom: string;
  nomEntreprise?: string;
  actif: boolean;
  type: TypeUtilisateur;
  adresse?: string;
  specialite?: string;
  createdAt: string;
}
export interface ActivationCompteRequest {
  token: string;
  motDePasse: string;
}
