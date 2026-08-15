import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval, Subject, merge } from 'rxjs';
import { switchMap, startWith, shareReplay } from 'rxjs/operators';
import { environment } from '../../../../../environments/environment';
import { AppNotification } from '../../../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly baseUrl = `${environment.apiUrl}/notifications`;
  private refreshTrigger$ = new Subject<void>();

  nonLuesCount$: Observable<number> = merge(
    interval(15000).pipe(startWith(0)),
    this.refreshTrigger$,
  ).pipe(
    switchMap(() => this.compterNonLues()),
    shareReplay(1),
  );

  constructor(private http: HttpClient) {}

  lister(): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>(this.baseUrl);
  }

  compterNonLues(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/non-lues/count`);
  }

  marquerLue(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/${id}/lire`, {});
  }

  rafraichir(): void {
    this.refreshTrigger$.next();
  }
}
