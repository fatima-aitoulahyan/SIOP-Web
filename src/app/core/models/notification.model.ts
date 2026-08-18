export enum TypeNotification {
  NOUVEAU_TRAVAIL_ASSIGNE = 'NOUVEAU_TRAVAIL_ASSIGNE',
  TRAVAIL_TERMINE = 'TRAVAIL_TERMINE',
  TRAVAIL_ANNULE = 'TRAVAIL_ANNULE',
  DEMANDE_REJETEE = 'DEMANDE_REJETEE',
  EVALUATION_A_VALIDER = 'EVALUATION_A_VALIDER',
}

export interface AppNotification {
  id: number;
  type: TypeNotification;
  titre: string;
  message: string;
  entiteType: string;
  entiteId: number;
  lu: boolean;
  dateCreation: string;
}
