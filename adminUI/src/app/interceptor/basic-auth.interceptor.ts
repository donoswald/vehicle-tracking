import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class BasicAuthInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // add authorization header with basic auth credentials if available
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser) {
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${currentUser}`
        }
      });
    }
    return next.handle(request);
  }
}
