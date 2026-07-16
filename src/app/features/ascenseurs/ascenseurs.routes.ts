import { Routes } from '@angular/router';
import { roleGuard } from '../../core/auth/role-guard';
import { CreerAscenseur } from './creer-ascenseur/creer-ascenseur';

export const ASCENSEUR_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./liste-ascenseurs/liste-ascenseurs').then((m) => m.ListeAscenseurs),
  },
  {
    path: 'nouveau',
    loadComponent: () => import('./creer-ascenseur/creer-ascenseur').then((m) => m.CreerAscenseur),
    canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./ascenseur-detail/ascenseur-detail').then((m) => m.AscenseurDetailComponent),
  },
  {
    path: ':id/modifier',
    loadComponent: () => import('./creer-ascenseur/creer-ascenseur').then((m) => m.CreerAscenseur),
    canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
  },
];
