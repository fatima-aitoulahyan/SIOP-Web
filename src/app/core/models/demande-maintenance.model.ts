// =====================================================
// Enums (miroir du backend : maintenance/enums/*)
// =====================================================

export enum TypeDemande {
  PANNE = 'PANNE',
  ENTRETIEN_PREVENTIF = 'ENTRETIEN_PREVENTIF',
  BRUIT_ANORMAL = 'BRUIT_ANORMAL',
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
}

// =====================================================
// Libellés d'affichage (français)
// =====================================================

export const TYPE_DEMANDE_LABELS: Record<TypeDemande, string> = {
  [TypeDemande.PANNE]: 'Panne',
  [TypeDemande.ENTRETIEN_PREVENTIF]: 'Entretien préventif',
  [TypeDemande.BRUIT_ANORMAL]: 'Bruit anormal',
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
};

// =====================================================
// DTOs (miroir du backend : dto/demandeMaintenance/*)
// =====================================================

export interface DemandeMaintenanceCreateDTO {
  ascenseurId: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  description: string;
  dateSouhaitee?: string | null;
}

export interface DemandeMaintenanceDTO {
  id: number;
  typeDemande: TypeDemande;
  priorite: PrioriteDemande;
  statut: StatutDemande;
  description: string;
  dateSouhaitee?: string | null;

  ascenseurId: number;
  ascenseurNom: string;

  clientId: number;

  dateCreation?: string | null;
}
