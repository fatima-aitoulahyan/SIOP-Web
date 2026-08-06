import { ApplicationConfig, provideZoneChangeDetection, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { firstValueFrom } from 'rxjs';

import { routes } from './app.routes';
import { jwtInterceptor } from './core/http/jwt-interceptor';
import { errorInterceptor } from './core/http/error-interceptor';
import { AuthService } from './core/auth/auth';

export function initializeAuth(authService: AuthService) {
  return () => {
    const token = authService.getToken();
    if (!token) {
      authService.authReady.set(true);
      return Promise.resolve();
    }
    return firstValueFrom(authService.me())
      .catch(() => authService.logout())
      .finally(() => authService.authReady.set(true));
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor, errorInterceptor])),
    provideAnimations(),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [AuthService],
      multi: true,
    },
  ],
};
