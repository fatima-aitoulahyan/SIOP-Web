import { Composant } from './composant.model';

export type TypeAssemblage = 'CABINE' | 'GAINE' | 'MACHINERIE' | 'PORTE' | 'ELECTRIQUE' | 'AUTRE';

export interface Assemblage {
  id: number;
  nom: string;
  reference: string;
  type: TypeAssemblage;
  fabricant?: string;
  dateInstallation?: string;
  dureeVieEstimeeMois?: number;
  description?: string;
  imageUrl?: string;
  niveau: number;
  ascenseurId: number;
  parentId?: number;
  parentNom?: string;
}

export interface AssemblageCreateDTO {
  nom: string;
  reference: string;
  type: TypeAssemblage;
  fabricant?: string;
  dateInstallation?: string;
  dureeVieEstimeeMois?: number;
  description?: string;
  imageUrl?: string;
  niveau?: number;
  ascenseurId: number;
  parentId?: number | null;
}
export interface AssemblageUpdateDTO {
  nom: string;
  reference: string;
  type: TypeAssemblage;
  fabricant?: string;
  dateInstallation?: string;
  dureeVieEstimeeMois?: number;
  description?: string;
  imageUrl?: string;
  niveau: number;
}

export interface AssemblageTree {
  id: number;
  nom: string;
  reference: string;
  type: TypeAssemblage;
  niveau: number;
  imageUrl?: string;
  sousAssemblages: AssemblageTree[];
  composants: Composant[];
}
