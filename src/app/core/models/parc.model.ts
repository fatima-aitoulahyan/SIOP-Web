export interface ParcDTO {
  id: number;
  nom: string;
  nombreSites: number;
  nombreTechniciens: number;
  createdAt: string;
}

export interface ParcCreateDTO {
  nom: string;
}

import { SiteDTO } from './site.model';
import { UtilisateurResponseDTO } from './utilisateur.model';

export interface ParcDetailDTO extends ParcDTO {
  sites: SiteDTO[];
  techniciens: UtilisateurResponseDTO[];
}