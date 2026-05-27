import { InjectionToken } from '@angular/core';

export interface InitConfig {
  urlBackend: string;
  urlPortal?: string;
}

export const INIT_CONFIG = new InjectionToken<InitConfig>('window.initConfig');
