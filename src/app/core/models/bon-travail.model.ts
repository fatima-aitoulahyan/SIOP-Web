import { PieceJointeAvecUrlDTO } from './maintenance.model';
import { PrioriteDemande } from './maintenance.model';
import { TechnicienResumeDTO } from './technicien.model';

export enum StatutBonTravail {
  PLANIFIE = 'PLANIFIE',
  EN_COURS = 'EN_COURS',
  TERMINE = 'TERMINE',
  ANNULE = 'ANNULE',
}

export enum StatutItem {
  NON_VERIFIE = 'NON_VERIFIE',
  CONFORME = 'CONFORME',
  ANOMALIE_DETECTEE = 'ANOMALIE_DETECTEE',
}

export enum GraviteAnomalie {
  MINEURE = 'MINEURE',
  MAJEURE = 'MAJEURE',
  CRITIQUE = 'CRITIQUE',
}

export interface BonTravailResumeDTO {
  id: number;
  statut: StatutBonTravail;
  priorite: PrioriteDemande;
  dateInterventionPrevue: string;
  ascenseurNom: string;
  siteAdresse: string;
  parcNom: string;
  technicienResponsableNom: string;
}

export interface BonTravailDTO {
  id: number;
  statut: StatutBonTravail;
  priorite: PrioriteDemande;
  dateInterventionPrevue: string;
  description: string | null;
  demandeMaintenanceId: number | null;
  ascenseurId: number | null;
  ascenseurNom: string;
  siteAdresse: string | null;
  parcId: number | null;
  parcNom: string | null;
  technicienResponsableId: number;
  technicienResponsableNom: string;
  techniciens: TechnicienResumeDTO[];
  dureeEstimeeMinutes: number;
  dateDebutReelle: string | null;
  dateFinReelle: string | null;
  diagnostic: string | null;
  causeIdentifiee: string | null;
  actionRealisee: string | null;
  piecesRemplacees: string | null;
  essaiConcluant: boolean | null;
  recommandations: string | null;
  photosDemande: PieceJointeAvecUrlDTO[];
  piecesJointesBonTravail: PieceJointeAvecUrlDTO[];
  createdAt: string;
}

export interface ClotureBonTravailDTO {
  diagnostic: string;
  causeIdentifiee: string | null;
  actionRealisee: string;
  piecesRemplacees: string | null;
  essaiConcluant: boolean | null;
  recommandations: string | null;
}

export interface BonTravailCreateDTO {
  demandeMaintenanceId?: number | null;
  ascenseurId?: number | null;
  parcId?: number | null;
  siteId?: number | null;
  technicienResponsableId: number;
  technicienIdsRenfort?: number[];
  dateInterventionPrevue: string;
  dureeEstimeeMinutes: number;
  priorite?: PrioriteDemande | null;
  description?: string | null;
  visitePreventive: boolean;
}

export interface ItemCheckListDTO {
  id: number;
  ordre: number;
  libelle: string;
  statut: StatutItem;
  gravite: GraviteAnomalie | null;
  remarque: string | null;
  piecesJointes: PieceJointeAvecUrlDTO[];
}

export interface ItemCheckListUpdateDTO {
  statut: StatutItem;
  gravite?: GraviteAnomalie | null;
  remarque?: string | null;
}

export interface ClotureChecklistDTO {
  bilanIntervention: string;
  estMaintenance: boolean;
  estDepannage: boolean;
  estTravaux: boolean;
}

export interface ChecklistMaintenanceDTO {
  id: number;
  mois: number;
  annee: number;
  ascenseurId: number;
  ascenseurNom: string;
  bonTravailId: number;
  technicienId: number | null;
  technicienNom: string | null;
  heureArrivee: string | null;
  heureDepart: string | null;
  estMaintenance: boolean;
  estDepannage: boolean;
  estTravaux: boolean;
  bilanIntervention: string | null;
  items: ItemCheckListDTO[];
}

export interface ConflitTechnicienDTO {
  technicienId: number;
  technicienNom: string;
  bonTravailConflitId: number;
  ascenseurNom: string;
  dateInterventionPrevue: string;
}
export interface CommentaireDTO {
  id: number;
  auteurId: number;
  auteurNom: string;
  auteurRole: 'TECHNICIEN' | 'RESPONSABLE_MAINTENANCE' | string;
  contenu: string;
  createdAt: string;
}
export const STATUT_BON_TRAVAIL_LABELS: Record<StatutBonTravail, string> = {
  [StatutBonTravail.PLANIFIE]: 'Planifié',
  [StatutBonTravail.EN_COURS]: 'En cours',
  [StatutBonTravail.TERMINE]: 'Terminé',
  [StatutBonTravail.ANNULE]: 'Annulé',
};
