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

/**
 * Libellé d'affichage d'un client pour les listes déroulantes.
 * `nomEntreprise` étant facultatif, on retombe sur « Prénom Nom »
 * puis sur l'email afin de ne jamais afficher une option vide.
 */
export function libelleClient(
  c: Pick<UtilisateurResponseDTO, 'nomEntreprise' | 'nom' | 'prenom' | 'email'>,
): string {
  if (c.nomEntreprise?.trim()) {
    return c.nomEntreprise.trim();
  }
  const nomComplet = `${c.prenom ?? ''} ${c.nom ?? ''}`.trim();
  return nomComplet || c.email || 'Client sans nom';
}
