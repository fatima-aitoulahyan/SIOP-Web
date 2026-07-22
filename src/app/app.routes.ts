import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth-guard';
import { roleGuard } from './core/auth/role-guard';
import { ResponsableLayout } from './core/layout/responsable-layout/responsable-layout';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.LoginComponent),
  },

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'utilisateurs',
    loadChildren: () =>
      import('./features/utilisateurs/utilisateurs.routes').then((m) => m.UTILISATEURS_ROUTES),
  },
  {
    path: 'dashboard/admin',
    loadComponent: () =>
      import('./features/dashboard/dashboard-admin/dashboard-admin').then((m) => m.DashboardAdmin),
    canActivate: [roleGuard(['ADMINISTRATEUR'])],
  },
  {
    path: 'activation-compte',
    loadComponent: () =>
      import('./features/utilisateurs/activation-compte/activation-compte').then(
        (m) => m.ActivationCompte,
      ),
  },
  {
    path: 'maintenance',
    loadChildren: () =>
      import('./features/maintenance/maintenance.routes').then((m) => m.MAINTENANCE_ROUTES),
    canActivate: [roleGuard(['CLIENT'])],
  },

  {
    path: '',
    component: ResponsableLayout,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard/responsable-maintenance',
        loadComponent: () =>
          import('./features/dashboard/dashboard-responsable-maintenance/dashboard-responsable-maintenance').then(
            (m) => m.DashboardResponsableMaintenance,
          ),
        canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
      },
      {
        path: 'ascenseurs',
        loadChildren: () =>
          import('./features/ascenseurs/ascenseurs.routes').then((m) => m.ASCENSEUR_ROUTES),
      },
      {
        path: 'sites',
        loadChildren: () => import('./features/sites/site.routes').then((m) => m.SITE_ROUTES),
        canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR'])],
      },
    ],
  },
];
