/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Routes } from '@angular/router';

export const CLIENTE_ROUTES: Routes = [

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
    path: 'cadastro',
    loadComponent: () => import('./cadastro/cadastro.componente').then(c => c.CadastroComponente)
  },

  {
    path: 'cadastro/:id',
    loadComponent: () => import('./cadastro/cadastro.componente').then(c => c.CadastroComponente)
  }

];
