import { Ville } from './ville.model';

export interface SiteDTO {
  id: number;
  ville: Ville;
  adresse: string;
  codePostal: string | null;
  clientId: number;
  clientNom: string;
}

export interface SiteCreateDTO {
  clientId: number;
  ville: Ville;
  adresse: string;
  codePostal?: string | null;
}

export interface SiteUpdateDTO {
  ville?: Ville | null;
  adresse: string;
  codePostal?: string | null;
}

export interface ClientOptionDTO {
  id: number;
  nom: string;
}
