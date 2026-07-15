import {
  ApplicationConfig,
  ErrorHandler,
  importProvidersFrom,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';
import "zone.js";
import { routes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors, withInterceptorsFromDi, withXhr } from '@angular/common/http';
import { ToastrModule } from 'ngx-toastr';
import { INIT_CONFIG, InitConfig } from './config/init-config.token';
import { apcoreInterceptors, FaroErrorHandler, PARAMS } from '@andre.penteado/ngx-apcore';
import { DESCRICAO, LOGOTIPO, MODULO, PREFIXO_PERFIL_SISTEMA } from './config/layout';
import { menu } from './config/menu';
import localePT from '@angular/common/locales/pt';
import { registerLocaleData } from '@angular/common';

registerLocaleData(localePT);

const CONFIG = (window as any).initConfig as InitConfig;

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: ErrorHandler, useClass: FaroErrorHandler },
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideAnimations(),
    provideHttpClient(withXhr(), withInterceptorsFromDi()),
    provideHttpClient(withXhr(), withInterceptors(apcoreInterceptors)),
    importProvidersFrom(
      ToastrModule.forRoot()
    ),
    {
      provide: LOCALE_ID,
      useValue: 'pt-BR'
    },
    {
      provide: INIT_CONFIG,
      useValue: CONFIG
    },
    {
      provide: PARAMS,
      useValue: {
        logotipo: LOGOTIPO,
        menu: menu,
        sistema: MODULO,
        descricao: DESCRICAO,
        urlBackend: CONFIG.urlBackend,
        urlPortal: CONFIG.urlPortal,
        prefixoPerfil: PREFIXO_PERFIL_SISTEMA
      }
    }
  ]
};
