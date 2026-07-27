
export interface SiteDTO {
  id: number;
  villeNom: string;
  adresse: string;
  codePostal: string | null;
  clientId: number;
  clientNom: string;
  zone?: string;
}

export interface SiteCreateDTO {
  clientId: number;
  villeId: number;
  adresse: string;
  codePostal?: string | null;
  zone?: string;
}

export interface SiteUpdateDTO {
  villeId?: number | null;
  adresse: string;
  codePostal?: string | null;
  zone?: string;
}

export interface VilleDTO {
  id: number;
  nom: string;
}
