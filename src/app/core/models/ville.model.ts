export enum Ville {
  MARRAKECH = 'MARRAKECH',
  CASABLANCA = 'CASABLANCA',
  RABAT = 'RABAT',
}

export const VILLE_LABELS: Record<Ville, string> = {
  [Ville.MARRAKECH]: 'Marrakech',
  [Ville.CASABLANCA]: 'Casablanca',
  [Ville.RABAT]: 'Rabat',
};

/** Liste ordonnée des villes, pour alimenter les listes déroulantes. */
export const VILLES: Ville[] = Object.values(Ville);
