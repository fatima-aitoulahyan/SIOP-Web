export interface Assemblage {
  id: number;
  nom: string;
  description?: string;
  niveau: number;
  ascenseurId: number;
  parentId?: number;
  parentNom?: string;
}

export interface AssemblageCreateDTO {
  nom: string;
  description?: string;
  niveau: number;
  ascenseurId: number;
  parentId?: number | null;
}

export interface AssemblageUpdateDTO {
  nom: string;
  description?: string;
  niveau: number;
}

export interface AssemblageTree {
  id: number;
  nom: string;
  description?: string;
  niveau: number;
  sousAssemblages: AssemblageTree[];
}
