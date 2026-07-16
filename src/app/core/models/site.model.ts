
export interface SiteDTO {
  id: number;
  villeNom: string;
  adresse: string;
  codePostal: string | null;
  clientId: number;
  clientNom: string;
}

export interface SiteCreateDTO {
  clientId: number;
  villeId: number;
  adresse: string;
  codePostal?: string | null;
}

export interface SiteUpdateDTO {
  villeId?: number | null;
  adresse: string;
  codePostal?: string | null;
}

export interface VilleDTO {
  id: number;
  nom: string;
}

export interface ClientOptionDTO {
  id: number;
  nom: string;
}
