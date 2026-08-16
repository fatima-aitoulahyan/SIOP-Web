import { Routes } from '@angular/router';
import { roleGuard } from '../../core/auth/role-guard';

export const BON_TRAVAIL_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./bons-travail-list/bons-travail-list').then((m) => m.BonsTravailListComponent),
  },
  {
    path: 'nouveau',
    loadComponent: () =>
      import('./bons-travail-form/bons-travail-form').then((m) => m.BonsTravailFormComponent),
    canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR'])],
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./checklist-detail/checklist-detail').then((m) => m.ChecklistDetailComponent),
  },
];
