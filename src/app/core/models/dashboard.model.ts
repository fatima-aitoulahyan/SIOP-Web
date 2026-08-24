import { BonTravailResumeDTO } from './bon-travail.model';

// Statistiques du dashboard Responsable — calqué sur DashboardResponsableDTO.java
export interface DashboardResponsableStats {
  // Bilan du mois en cours
  demandesCeMois: number;
  resoluesCeMois: number;

  // Files d'attente actuelles
  enAttente: number;
  assignees: number;
  enCours: number;
  urgentesEnAttente: number;
  evaluationsAValider: number;

  // Anomalies critiques détectées dans les 30 derniers jours
  nombreAnomaliesCritiques: number;

  // Charge des techniciens aujourd'hui
  techniciensEnInterventionAujourdhui: number;
  techniciensTotal: number;
}

// Une anomalie critique détectée lors d'une intervention — calqué sur AnomalieCritiqueDTO.java
// NB : LocalDate côté Java arrive en "yyyy-MM-dd" côté JSON, donc string | null ici.
export interface AnomalieCritique {
  bonTravailId: number | null; // null si la checklist n'est liée à aucun BT
  ascenseurNom: string;
  siteAdresse: string;
  libelleItem: string;         // item de checklist en cause
  remarque: string | null;     // commentaire du technicien
  dateCloture: string | null;
}


// Statistiques du dashboard Admin — calqué sur DashboardAdminDTO.java
export interface DashboardAdminStats {
  // Section 1 — Structure du système
  nombreParcs: number;
  nombreSites: number;
  nombreAscenseurs: number;
  nombreClients: number;

  // Section 3 — Santé opérationnelle
  demandesTotales: number;
  demandesEnAttente: number;
  interventionsEnCours: number;
  tauxResolution: number;
  anomaliesCritiques: number;
}

// Répartition des utilisateurs par rôle — calqué sur RepartitionUtilisateursDTO.java
export interface RepartitionUtilisateurs {
  clients: number;
  techniciens: number;
  responsables: number;
  administrateurs: number;
  total: number;
}

// Activité d'un parc — calqué sur ActiviteParParcDTO.java
export interface ActiviteParParc {
  parcId: number;
  parcNom: string;
  nombreSites: number;
  nombreTechniciens: number;
  demandesEnAttente: number;
}

// Statistiques du dashboard Technicien — calqué sur DashboardTechnicienDTO.java
export interface DashboardTechnicienStats {
  interventionsAujourdhui: number;
  enCours: number;
  totalCeMois: number;
  termineesCeMois: number;
}

// Prochaine intervention — calqué sur ProchaineInterventionDTO.java
export interface ProchaineIntervention {
  intervention: BonTravailResumeDTO | null; // null si aucune
  contexte: 'AUJOURDHUI' | 'DEMAIN' | 'AUCUNE';
  enRetard: boolean; // true si heure passée et non démarrée
}

// Un jour du planning semaine — calqué sur PlanningJourDTO.java
// NB : LocalDate arrive en "yyyy-MM-dd" côté JSON
export interface PlanningJour {
  date: string;
  jourLabel: string; // "Lun", "Mar"...
  nombreInterventions: number;
}

// Statistiques du dashboard Client — calqué sur DashboardClientDTO.java
export interface DashboardClientStats {
  nombreAscenseurs: number;
  demandesTotal: number;
  assignees: number;
  enCours: number;
  resolues: number;
}

// Un ascenseur du client avec l'état de sa demande active — calqué sur AscenseurAvecEtatDTO.java
export interface AscenseurAvecEtat {
  ascenseurId: number;
  nom: string;
  siteAdresse: string | null;
  statutDemande: string | null;  // EN_ATTENTE / ASSIGNEE / EN_COURS, ou null si aucune demande
  aDemandeActive: boolean;
}

// Une demande du suivi client — calqué sur DemandeSuiviDTO.java
export interface DemandeSuivi {
  demandeId: number;
  ascenseurNom: string;           // "Évaluation · adresse" quand pas d'ascenseur
  typeDemande: string;
  statut: string;
  technicienNom: string | null;   // null tant qu'aucun bon de travail n'existe
  dateDemande: string | null;     // LocalDate Java → "yyyy-MM-dd"
}
