import { Routes } from '@angular/router';
import { roleGuard } from '../../core/auth/role-guard';

export const UTILISATEURS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./utilisateur-list/utilisateur-list').then((m) => m.UtilisateurList),
    canActivate: [roleGuard(['ADMINISTRATEUR'])],
  },
  {
    path: 'creer',
    loadComponent: () =>
      import('./utilisateur-form/utilisateur-form').then((m) => m.UtilisateurForm),
    canActivate: [roleGuard(['ADMINISTRATEUR'])],
  },
];
