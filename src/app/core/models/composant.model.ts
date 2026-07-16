export type TypeComposant =
  'MOTEUR' | 'FREIN' | 'CABLE' | 'CABINE' | 'PORTE' | 'CONTROLEUR' | 'CAPTEUR' | 'AUTRE';

export interface Composant {
  id: number;
  nom: string;
  reference: string;
  type: TypeComposant;
  fabricant?: string;
  dateInstallation?: string;
  dureeVieEstimeeMois?: number;
  actif: boolean;
  assemblageId: number;
  assemblageNom?: string;
}

export interface ComposantCreateDTO {
  nom: string;
  reference: string;
  type: TypeComposant;
  fabricant?: string;
  dateInstallation?: string;
  dureeVieEstimeeMois?: number;
  assemblageId: number;
}

export interface ComposantUpdateDTO {
  nom: string;
  reference: string;
  type: TypeComposant;
  fabricant?: string;
  dateInstallation?: string;
  dureeVieEstimeeMois?: number;
}
