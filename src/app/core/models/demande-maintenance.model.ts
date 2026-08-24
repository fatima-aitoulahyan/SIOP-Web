

export enum TypeDemande {
  PANNE = 'PANNE',
  ENTRETIEN_PREVENTIF = 'ENTRETIEN_PREVENTIF',
  BRUIT_ANORMAL = 'BRUIT_ANORMAL',
  EVALUATION = 'EVALUATION',
  AUTRE = 'AUTRE',
}

export enum PrioriteDemande {
  BASSE = 'BASSE',
  NORMALE = 'NORMALE',
  URGENTE = 'URGENTE',
}

export enum StatutDemande {
  EN_ATTENTE = 'EN_ATTENTE',
  ASSIGNEE = 'ASSIGNEE',
  EN_COURS = 'EN_COURS',
  RESOLUE = 'RESOLUE',
  ANNULEE = 'ANNULEE',
  REJETEE = 'REJETEE',
}


export const TYPE_DEMANDE_LABELS: Record<TypeDemande, string> = {
  [TypeDemande.PANNE]: 'Panne',
  [TypeDemande.ENTRETIEN_PREVENTIF]: 'Maintenance préventive',
  [TypeDemande.BRUIT_ANORMAL]: 'Bruit anormal',
  [TypeDemande.EVALUATION]: "Évaluation d'un nouvel ascenseur",
  [TypeDemande.AUTRE]: 'Autre',
};

export const PRIORITE_DEMANDE_LABELS: Record<PrioriteDemande, string> = {
  [PrioriteDemande.BASSE]: 'Basse',
  [PrioriteDemande.NORMALE]: 'Normale',
  [PrioriteDemande.URGENTE]: 'Urgente',
};

export const STATUT_DEMANDE_LABELS: Record<StatutDemande, string> = {
  [StatutDemande.EN_ATTENTE]: 'En attente',
  [StatutDemande.ASSIGNEE]: 'Assignée',
  [StatutDemande.EN_COURS]: 'En cours',
  [StatutDemande.RESOLUE]: 'Résolue',
  [StatutDemande.ANNULEE]: 'Annulée',
  [StatutDemande.REJETEE]: 'Rejetée',
};



export interface PieceJointeAvecUrlDTO {
  id: number;
  nomFichier: string;
  url: string;
  typeFichier: string;
  description: string | null;
}


export interface DemandeMaintenanceCreateDTO {
  ascenseurId: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  description: string;
  dateSouhaitee?: string | null;
}

export interface DemandeEvaluationCreateDTO {
  ville: string;
  adresse: string;
  description: string;
  dateSouhaitee?: string | null;
}

export interface RejetDemandeDTO {
  motif: string;
}

export interface DemandeMaintenanceDTO {
  id: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  statut: StatutDemande;
  description: string;
  dateSouhaitee: string | null;
  motifRejet: string | null;

  ascenseurId: number | null;
  ascenseurNom: string | null;

  villeSaisie: string | null;
  adresseSaisie: string | null;

  clientId: number;
  clientNom: string;

  createdAt: string;
  dateResolution: string | null;
  photos: PieceJointeAvecUrlDTO[];
}
