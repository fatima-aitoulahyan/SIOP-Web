import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth-guard';
import { roleGuard } from './core/auth/role-guard';
import { ResponsableLayout } from './core/layout/responsable-layout/responsable-layout';
import { Routes } from '@angular/router';
import { ClientLayout } from './core/layout/client-layout/client-layout';

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
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password').then(
        (m) => m.ForgotPasswordComponent,
      ),
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./features/auth/reset-password/reset-password').then((m) => m.ResetPasswordComponent),
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

  // ---- Espace Admin ----
  {
    path: '',
    loadComponent: () =>
      import('./core/layout/admin-layout/admin-layout').then((m) => m.AdminLayoutComponent),
    canActivate: [authGuard, roleGuard(['ADMINISTRATEUR'])],
    children: [
      {
        path: 'dashboard/admin',
        loadComponent: () =>
          import('./features/dashboard/dashboard-admin/dashboard-admin').then(
            (m) => m.DashboardAdmin,
          ),
        canActivate: [roleGuard(['ADMINISTRATEUR'])],
      },
      {
        path: 'utilisateurs',
        loadChildren: () =>
          import('./features/utilisateurs/utilisateurs.routes').then((m) => m.UTILISATEURS_ROUTES),
        canActivate: [roleGuard(['ADMINISTRATEUR'])],
      },
    ],
  },

  // ---- Espace Responsable maintenance ----
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
        canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
      },
      {
        path: 'sites',
        loadChildren: () => import('./features/sites/site.routes').then((m) => m.SITE_ROUTES),
        canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE', 'ADMINISTRATEUR'])],
      },
      {
        path: 'responsable',
        canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
        children: [
          {
            path: 'demandes',
            loadChildren: () =>
              import('./features/maintenance/maintenance.routes').then(
                (m) => m.MAINTENANCE_DEMANDES_ROUTES,
              ),
          },
          {
            path: 'bons-travail',
            loadChildren: () =>
              import('./features/bon-travail/bon-travail.routes').then((m) => m.BON_TRAVAIL_ROUTES),
          },
          {
            path: ':id',
            loadComponent: () =>
              import('./features/maintenance/maintenance-gestion/maintenance-gestion').then(
                (m) => m.MaintenanceGestionComponent,
              ),
          },
        ],
      },
      {
        path: 'parcs',
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./features/parcs/parc-list/parc-list').then((m) => m.ParcListComponent),
          },
          {
            path: 'nouveau',
            loadComponent: () =>
              import('./features/parcs/parc-form/parc-form').then((m) => m.ParcFormComponent),
            canActivate: [roleGuard(['ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE'])],
          },
          {
            path: ':id',
            loadComponent: () =>
              import('./features/parcs/parc-detail/parc-detail').then((m) => m.ParcDetailComponent),
          },
          {
            path: ':id/modifier',
            loadComponent: () =>
              import('./features/parcs/parc-form/parc-form').then((m) => m.ParcFormComponent),
            canActivate: [roleGuard(['ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE'])],
          },
        ],
      },
    ],
  },

  // ---- Espace Client ----
  {
    path: '',
    component: ClientLayout,
    canActivate: [authGuard, roleGuard(['CLIENT'])],
    children: [
      {
        path: 'client',
        loadChildren: () =>
          import('./features/maintenance/maintenance.routes').then((m) => m.MAINTENANCE_ROUTES),
        data: { mode: 'client' },
      },
    ],
  },

  // ---- Espace Technicien ----
  {
    path: '',
    component: ResponsableLayout,
    canActivate: [authGuard, roleGuard(['TECHNICIEN'])],
    children: [
      {
        path: 'technicien',
        children: [
          {
            path: 'demandes',
            loadChildren: () =>
              import('./features/maintenance/maintenance.routes').then(
                (m) => m.MAINTENANCE_DEMANDES_ROUTES,
              ),
          },
          {
            path: 'bons-travail',
            loadChildren: () =>
              import('./features/bon-travail/bon-travail.routes').then((m) => m.BON_TRAVAIL_ROUTES),
          },
        ],
      },
    ],
  },
];
