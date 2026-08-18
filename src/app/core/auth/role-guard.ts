import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, map, take } from 'rxjs';
import { AuthService } from './auth';

export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);

    return toObservable(authService.authReady).pipe(
      filter((ready) => ready),
      take(1),
      map(() => {
        const role = authService.currentRole();
        return !!role && allowedRoles.includes(role);
      }),
    );
  };
}
