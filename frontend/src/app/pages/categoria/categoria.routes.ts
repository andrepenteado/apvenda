/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Routes } from '@angular/router';

export const CATEGORIA_ROUTES: Routes = [

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
