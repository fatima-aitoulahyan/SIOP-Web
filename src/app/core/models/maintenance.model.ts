export enum StatutDemande {
  EN_ATTENTE = 'EN_ATTENTE',
  ASSIGNEE = 'ASSIGNEE',
  EN_COURS = 'EN_COURS',
  RESOLUE = 'RESOLUE',
  ANNULEE = 'ANNULEE',
  REJETEE = 'REJETEE',
}

export enum PrioriteDemande {
  BASSE = 'BASSE',
  NORMALE = 'NORMALE',
  URGENTE = 'URGENTE',
}

export enum TypeDemande {
  PANNE = 'PANNE',
  ENTRETIEN_PREVENTIF = 'ENTRETIEN_PREVENTIF',
  BRUIT_ANORMAL = 'BRUIT_ANORMAL',
  AUTRE = 'AUTRE',
}

export interface DemandeMaintenanceDTO {
  id: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  statut: StatutDemande;
  description: string;
  dateSouhaitee: string | null;
  motifRejet: string | null;
  ascenseurId: number;
  ascenseurNom: string;
  clientId: number;
  clientNom: string;
  createdAt: string;
  photos: PieceJointeAvecUrlDTO[];
}

export interface DemandeMaintenanceCreateDTO {
  ascenseurId: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  description: string;
  dateSouhaitee?: string | null;
}
export interface PieceJointeAvecUrlDTO {   
  id: number;
  nomFichier: string;
  url: string;
  typeFichier: string;
  description: string | null;
}

export interface RejetDemandeDTO {
  motif: string;
}