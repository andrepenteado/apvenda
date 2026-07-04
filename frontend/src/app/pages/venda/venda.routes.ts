/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Routes } from '@angular/router';

export const VENDA_ROUTES: Routes = [

  {
    path: '',
    redirectTo: 'pesquisar',
    pathMatch: 'full'
  },

  {
    path: 'pesquisar',
    loadComponent: () => import('./pesquisar/pesquisar.componente').then(c => c.PesquisarComponente)
  },

  {
    path: 'consultar/:id',
    loadComponent: () => import('./consultar/consultar.componente').then(c => c.ConsultarComponente)
  }

];
