import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { setMessage } from './states/action/message.action';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) { }
  private store = inject(Store)

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          switch (error.error.msg) {
            case "bad credentials":
              this.setMessage('Incorrect username or password.')
              break;

            default:
              this.setMessage('Unauthorised')
              break;
          }
          this.router.navigate(['login']);
        }
        return throwError(() => error);
      })
    );
  }

  setMessage(msg: string) {
    this.store.dispatch(setMessage({ message: msg }));
  }
}
