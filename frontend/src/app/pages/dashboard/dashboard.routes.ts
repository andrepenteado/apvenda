/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 23:20:00 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Routes } from '@angular/router';

export const DASHBOARD_ROUTES: Routes = [

  {
    path: '',
    loadComponent: () => import('./dashboard.componente').then(c => c.DashboardComponente)
  }

];
