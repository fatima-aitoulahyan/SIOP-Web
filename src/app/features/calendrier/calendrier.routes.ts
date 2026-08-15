import { Routes } from '@angular/router';

export const CALENDRIER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./calendrier-page/calendrier-page').then((m) => m.CalendrierPage),
  },
];
