import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth-guard';
import { roleGuard } from './core/auth/role-guard';
import { ClientLayout } from './core/layout/client-layout/client-layout';
import { AdminLayoutComponent } from './core/layout/admin-layout/admin-layout';
import { TechnicienLayoutComponent } from './core/layout/technicien-layout/technicien-layout';

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

  // ---- Espace Administrateur & Responsable de Maintenance (Layout Unique) ----
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard, roleGuard(['ADMINISTRATEUR', 'RESPONSABLE_MAINTENANCE'])],
    children: [
      // Dashboards spécifiques
      {
        path: 'dashboard/admin',
        loadComponent: () =>
          import('./features/dashboard/dashboard-admin/dashboard-admin').then(
            (m) => m.DashboardAdmin,
          ),
        canActivate: [roleGuard(['ADMINISTRATEUR'])],
      },
      {
        path: 'dashboard/responsable-maintenance',
        loadComponent: () =>
          import('./features/dashboard/dashboard-responsable-maintenance/dashboard-responsable-maintenance').then(
            (m) => m.DashboardResponsableMaintenance,
          ),
        canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
      },

      // Gestion des utilisateurs (Réservé Admin)
      {
        path: 'utilisateurs',
        loadChildren: () =>
          import('./features/utilisateurs/utilisateurs.routes').then((m) => m.UTILISATEURS_ROUTES),
        canActivate: [roleGuard(['ADMINISTRATEUR'])],
      },

      // Fonctionnalités partagées (Ascenseurs, Sites, Parcs)
      {
        path: 'ascenseurs',
        loadChildren: () =>
          import('./features/ascenseurs/ascenseurs.routes').then((m) => m.ASCENSEUR_ROUTES),
      },
      {
        path: 'sites',
        loadChildren: () => import('./features/sites/site.routes').then((m) => m.SITE_ROUTES),
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
          },
        ],
      },

      // Espace Responsable (Demandes, Bons de travail, Calendrier, Évaluations)
      {
        path: 'responsable',
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
            path: 'calendrier',
            loadChildren: () =>
              import('./features/calendrier/calendrier.routes').then((m) => m.CALENDRIER_ROUTES),
          },
          {
            path: 'evaluations',
            loadComponent: () =>
              import('./features/maintenance/evaluation-ascenseur/evaluations-demandes-list/evaluations-demandes-list').then(
                (m) => m.EvaluationsDemandesListComponent,
              ),
          },
          {
            path: 'evaluations-en-attente',
            loadComponent: () =>
              import('./features/maintenance/evaluation-ascenseur/evaluations-en-attente/evaluations-en-attente').then(
                (m) => m.EvaluationsEnAttenteComponent,
              ),
          },
          {
            path: 'evaluations/:id',
            loadComponent: () =>
              import('./features/maintenance/evaluation-ascenseur/evaluation-detail/evaluation-detail').then(
                (m) => m.EvaluationDetailComponent,
              ),
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

  // ---- Espace Technicien (Correction : Utilise TechnicienLayoutComponent) ----
  {
    path: '',
    component: TechnicienLayoutComponent,
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
          {
            path: 'evaluations',
            loadChildren: () =>
              import('./features/maintenance/evaluation-ascenseur/evaluation-ascenseur.routes').then(
                (m) => m.EVALUATION_TECHNICIEN_ROUTES,
              ),
          },
          {
            path: 'interventions',
            loadChildren: () =>
              import('./features/bon-travail/bon-travail.routes').then((m) => m.BON_TRAVAIL_ROUTES),
          },
          {
            path: 'calendrier',
            loadChildren: () =>
              import('./features/calendrier/calendrier.routes').then((m) => m.CALENDRIER_ROUTES),
          },
        ],
      },
    ],
  },
];
