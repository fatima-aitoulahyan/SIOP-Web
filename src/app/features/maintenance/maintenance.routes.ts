import { Routes } from '@angular/router';

export const MAINTENANCE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./mes-demandes/mes-demandes').then((m) => m.MesDemandesComponent),
  },
  {
    path: 'nouvelle',
    loadComponent: () =>
      import('./creer-demande/creer-demande').then((m) => m.CreerDemandeComponent),
  },
];
