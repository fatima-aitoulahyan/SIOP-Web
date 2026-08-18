import { Routes } from '@angular/router';
export const SITE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./site-list/site-list').then((m) => m.SiteListComponent),
  },
  {
    path: 'nouveau',
    loadComponent: () => import('./site-form/site-form').then((m) => m.SiteFormComponent),
  },
  {
    path: ':id/modifier',
    loadComponent: () => import('./site-form/site-form').then((m) => m.SiteFormComponent),
  },

];
