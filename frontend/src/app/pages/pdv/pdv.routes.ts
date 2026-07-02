/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Routes } from '@angular/router';

export const PDV_ROUTES: Routes = [

  {
    path: '',
    loadComponent: () => import('./pdv.componente').then(c => c.PdvComponente)
  }

];
