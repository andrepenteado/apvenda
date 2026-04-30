import {
  ApplicationConfig,
  importProvidersFrom,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';
import "zone.js";
import { routes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ToastrModule } from 'ngx-toastr';
import { INIT_CONFIG, InitConfig } from './config/init-config.token';
import { HttpErrorsInterceptor, PARAMS, WithCredentialsInterceptor } from '@andre.penteado/ngx-apcore';
import { LOGOTIPO, MODULO, PREFIXO_PERFIL_SISTEMA } from './config/layout';
import { menu } from './config/menu';
import localePT from '@angular/common/locales/pt';
import { registerLocaleData } from '@angular/common';

registerLocaleData(localePT);

const CONFIG = (window as any).initConfig as InitConfig;

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()),
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
        urlBackend: CONFIG.urlBackend,
        prefixoPerfil: PREFIXO_PERFIL_SISTEMA
      }
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorsInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: WithCredentialsInterceptor,
      multi: true
    }
  ]
};
