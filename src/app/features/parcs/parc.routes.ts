import { Routes } from '@angular/router';
import { roleGuard } from '../../core/auth/role-guard';

export const PARC_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./parc-list/parc-list').then((m) => m.ParcListComponent),
  },
  {
    path: 'nouveau',
    loadComponent: () => import('./parc-form/parc-form').then((m) => m.ParcFormComponent),
    canActivate: [roleGuard(['ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE'])],
  },
  {
    path: ':id',
    loadComponent: () => import('./parc-detail/parc-detail').then((m) => m.ParcDetailComponent),
  },
  {
    path: ':id/modifier',
    loadComponent: () => import('./parc-form/parc-form').then((m) => m.ParcFormComponent),
    canActivate: [roleGuard(['ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE'])],
  },
];