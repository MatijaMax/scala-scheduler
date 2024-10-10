import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpInterceptor,
  HttpEvent
} from '@angular/common/http';
import { Observable} from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(public auth: AuthService) { }
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("TOKENIZER IS NOT WORKING")
    if (this.auth.tokenIsPresent()) {
      console.log("TOKENIZER IS WORKING")
      console.log(this.auth.getToken())
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${this.auth.getToken()}` 
        }
      });
    }
    return next.handle(request);
  }
}