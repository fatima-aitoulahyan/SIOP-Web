export enum TypeAscenseur {
  TRACTION = 'TRACTION',
  MRL = 'MRL',
  HYDRAULIQUE = 'HYDRAULIQUE',
}

export interface AscenseurCreateDTO {
  clientId: number;
  siteId?: number;
  nom: string;
  description?: string;
  coutAcquisition?: number;
  fabricant: string;
  marque: string;
  modele?: string;
  numeroSerie?: string;
  codeBarre?: string;
  puissance?: string;
  nombreEtages?: number;
  capacitePersonnes?: number;
  chargeMaxKg?: number;
  vitesse?: number;
  type?: TypeAscenseur;
  dateMiseEnService?: string;
  dateExpirationGarantie?: string;
  informationsSupplementaires?: string;
}

export interface AscenseurUpdateDTO {
  nom: string;
  siteId: number;
  description?: string;
  coutAcquisition?: number;
  fabricant: string;
  marque: string;
  modele?: string;
  numeroSerie?: string;
  codeBarre?: string;
  puissance?: string;
  nombreEtages?: number;
  capacitePersonnes?: number;
  chargeMaxKg?: number;
  vitesse?: number;
  type?: TypeAscenseur;
  dateMiseEnService?: string;
  dateExpirationGarantie?: string;
  informationsSupplementaires?: string;
}

export interface AscenseurDTO {
  id: number;
  nom: string;
  description?: string;
  coutAcquisition?: number;
  fabricant: string;
  marque: string;
  modele?: string;
  numeroSerie?: string;
  codeBarre?: string;
  puissance?: string;
  nombreEtages?: number;
  capacitePersonnes?: number;
  chargeMaxKg?: number;
  vitesse?: number;
  type?: TypeAscenseur;
  dateMiseEnService?: string;
  dateExpirationGarantie?: string;
  informationsSupplementaires?: string;
  actif: boolean;

  clientId: number;
  clientNom?: string;
  clientPrenom?: string;
  clientNomEntreprise?: string;

  siteId: number;
  siteAdresse?: string;
  parcId: number | null;
  parcNom?: string;
  piecesJointes?: PieceJointeDTO[];
}
export interface PieceJointeDTO {
  id: number;
  nomFichier: string;
  url: string;
  typeFichier: string;
  description?: string;
}
