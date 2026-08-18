import { Routes } from '@angular/router';
import { roleGuard } from '../../core/auth/role-guard';

export const MAINTENANCE_DEMANDES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./maintenance-list/maintenance-list').then((m) => m.MaintenanceListComponent),
  },
  {
    path: 'nouveau',
    loadComponent: () =>
      import('./creer-demande/creer-demande-mantenance/creer-demande-mantenance').then(
        (m) => m.CreerDemandeMantenanceComponent,
      ),
    canActivate: [roleGuard(['CLIENT'])],
  },
  {
    path: 'nouvelle-evaluation',
    loadComponent: () =>
      import('./creer-demande/creer-demande-evaluation/creer-demande-evaluation').then(
        (m) => m.CreerDemandeEvaluationComponent,
      ),
    canActivate: [roleGuard(['CLIENT'])],
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./demande-detail/demande-detail').then((m) => m.DemandeDetailComponent),
  },
];

export const MAINTENANCE_ROUTES: Routes = [
  {
    path: 'demandes',
    children: MAINTENANCE_DEMANDES_ROUTES,
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./maintenance-gestion/maintenance-gestion').then(
        (m) => m.MaintenanceGestionComponent,
      ),
    canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR'])],
  },
];
