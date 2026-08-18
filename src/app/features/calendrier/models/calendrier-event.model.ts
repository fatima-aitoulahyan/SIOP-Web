export type SourceEvenement = 'BON_TRAVAIL' | 'EVENEMENT';

export type TypeEvenementCalendrier = 'REUNION' | 'CONGE' | 'FORMATION' | 'AUTRE';

export interface CalendrierEventDTO {
  id: string;
  titre: string;
  source: SourceEvenement;
  type: string;
  debut: string; // ISO LocalDateTime
  fin: string;
  lieu: string | null;
  technicienIds: number[];
  technicienNoms: string[];
  couleur: string;
}

export interface EvenementRequestDTO {
  titre: string;
  description?: string;
  type: TypeEvenementCalendrier;
  dateDebut: string; // ISO LocalDateTime
  dateFin: string;
  lieu?: string;
  technicienIds: number[];
}
