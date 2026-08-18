import { Routes } from '@angular/router';
import { roleGuard } from '../../../core/auth/role-guard';

export const EVALUATION_TECHNICIEN_ROUTES: Routes = [
  {
    path: ':bonTravailId',
    loadComponent: () =>
      import('./evaluation-ascenseur-form/evaluation-ascenseur-form').then(
        (m) => m.EvaluationAscenseurFormComponent,
      ),
    canActivate: [roleGuard(['TECHNICIEN'])],
  },
];

export const EVALUATION_RESPONSABLE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./evaluations-en-attente/evaluations-en-attente').then(
        (m) => m.EvaluationsEnAttenteComponent,
      ),
    canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./evaluation-detail/evaluation-detail').then((m) => m.EvaluationDetailComponent),
    canActivate: [roleGuard(['RESPONSABLE_MAINTENANCE'])],
  },
];
