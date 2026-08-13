// =====================================================
// Enums (miroir du backend : maintenance/enums/*)
// =====================================================

export enum TypeDemande {
  PANNE = 'PANNE',
  TRAVAUX = 'TRAVAUX',
  PREVENTIVE = 'PREVENTIVE',
  EVALUATION = 'EVALUATION',
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

// =====================================================
// Libellés d'affichage (français)
// =====================================================

export const TYPE_DEMANDE_LABELS: Record<TypeDemande, string> = {
  [TypeDemande.PANNE]: 'Panne',
  [TypeDemande.TRAVAUX]: 'Travaux',
  [TypeDemande.PREVENTIVE]: 'Maintenance préventive',
  [TypeDemande.EVALUATION]: "Évaluation d'un nouvel ascenseur",
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

// =====================================================
// Sous-types
// =====================================================

export interface PieceJointeAvecUrlDTO {
  id: number;
  nomFichier: string;
  url: string;
  typeFichier: string;
  description: string | null;
}

// =====================================================
// DTOs (miroir du backend : dto/DemandeMaintenance/*)
// =====================================================

export interface DemandeMaintenanceCreateDTO {
  ascenseurId: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  description: string;
  dateSouhaitee?: string | null;
}

// DTO dédié pour la demande d'évaluation (pas d'ascenseurId ni de siteId :
// ni l'un ni l'autre n'existent encore, le client tape ville/adresse en texte libre)
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
  photos: PieceJointeAvecUrlDTO[];
}
