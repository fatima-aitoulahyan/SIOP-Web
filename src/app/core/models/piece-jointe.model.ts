export enum TypeFichier {
  IMAGE = 'IMAGE',
  AUDIO = 'AUDIO',
  DOCUMENT = 'DOCUMENT',
  AUTRE = 'AUTRE',
}

export enum TypeEntiteJointe {
  ASCENSEUR = 'ASCENSEUR',
  ASSEMBLAGE = 'ASSEMBLAGE',
  DEMANDE_MAINTENANCE = 'DEMANDE_MAINTENANCE',
  INTERVENTION = 'INTERVENTION',
}

export interface PieceJointeDTO {
  id: number;
  nomFichier: string;
  typeFichier: TypeFichier | string;
  description?: string;
  url: string;
}
