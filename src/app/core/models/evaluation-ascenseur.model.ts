export enum StatutEvaluation {
  BROUILLON = 'BROUILLON',
  ENVOYEE = 'ENVOYEE',
  ACCEPTEE = 'ACCEPTEE',
  REFUSEE = 'REFUSEE',
}
export enum TypeAscenseur {
  TRACTION = 'TRACTION',
  HYDRAULIQUE = 'HYDRAULIQUE',
  MRL = 'MRL',
}
export interface EvaluationAscenseurDTO {
  id: number;
  bonTravailId: number;
  technicienId: number;
  technicienNom: string;

  dateVisite: string | null;
  nom: String | null;
  fabricant: string | null;
  marque: string | null;
  modele: string | null;
  numeroSerie: string | null;
  codeBarre: string | null;
  nombreEtages: number | null;
  capacitePersonnes: number | null;
  chargeMaxKg: number | null;
  vitesse: number | null;
  puissance: string | null;
  type: TypeAscenseur | null;
  dateMiseEnService: string | null;

  etatPortes: string | null;
  positionCabine: string | null;
  anomalies: string | null;
  causeExterieure: string | null;
  observations: string | null;

  statut: StatutEvaluation;
  motifRefus: string | null;
  responsableId: number | null;
  dateDecision: string | null;
  ascenseurCreeId: number | null;
}

export interface EvaluationAscenseurSoumissionDTO {
  nom: String;
  fabricant: string;
  marque?: string | null;
  modele?: string | null;
  numeroSerie?: string | null;
  codeBarre?: string | null;
  nombreEtages?: number | null;
  capacitePersonnes?: number | null;
  chargeMaxKg?: number | null;
  vitesse?: number | null;
  puissance?: string | null;
  type?: TypeAscenseur | null;
  dateMiseEnService?: string | null;

  etatPortes?: string | null;
  positionCabine?: string | null;
  anomalies?: string | null;
  causeExterieure?: string | null;
  observations?: string | null;
}

export interface EvaluationAscenseurValidationDTO {
  accepter: boolean;
  motif?: string | null;
}
