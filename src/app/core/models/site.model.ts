
export interface SiteDTO {
  id: number;
  villeNom: string;
  villeId: number;
  villeCodePostal: string;
  villeRegion: string;
  adresse: string;
  clientId: number;
  clientNom: string;
  parcId: number | null;
  parcNom: string | null;
}

export interface SiteCreateDTO {
  clientId: number;
  villeId: number;
  parcId: number;
  adresse: string;
}

export interface SiteUpdateDTO {
  villeId?: number | null;
  parcId?: number | null;
  adresse: string;
}
