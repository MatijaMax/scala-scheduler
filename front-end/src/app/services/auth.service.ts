import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../env/environment';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { LoggedUser } from '../model/logged-user';
import { RegisterUser } from '../model/register-user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  currentUser = new BehaviorSubject<LoggedUser>({
    username: '',
    authString: '',
  });
  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
    this.setUser();
  }
  
  login(user: any) {
    const loginHeaders = new HttpHeaders({
      Accept: 'application/json',
      'Content-Type': 'application/json',
    });
    const body = {
      username: user.email,
      password: user.password,
    };
    let options = { headers: loginHeaders };
    return this.http
      .post<any>(
        environment.apiHost + 'v1/login',
        JSON.stringify(body),
        options
      )
      .pipe(
        map((res) => {
          console.log('Login success');
          localStorage.setItem('username', res.username);
          localStorage.setItem('authString', res.authString);
          this.setUser();
        })
      );
  }

  tokenIsPresent() {
    let token = localStorage.getItem('authString');
    //console.log("ITS PRESENT")
    if (token == null) {
      return false;
    } else {
      //console.log("ITS PRESENT")
      return true;
    }
  }

  getToken() {
    //console.log(localStorage.getItem('authString'));
    return localStorage.getItem('authString');
  }

  logout() {
    localStorage.removeItem('username');
    localStorage.removeItem('authString');
    console.log('LOGGED OUT')
    this.router.navigate(['/login']);
    this.currentUser.next({ username: '', authString: '' });
  }

  setUser() {
    let token = localStorage.getItem('authString');
    if (token == null) {
      return;
    }
    const user: LoggedUser = {
      username: localStorage.getItem('username'),
      authString: localStorage.getItem('authString'),
    };
    this.currentUser.next(user);
    this.router.navigate(['/message']);
  }

  getCurrentUser() {
    const user: LoggedUser = {
      username: localStorage.getItem('username'),
      authString: localStorage.getItem('authString'),
    };
    return user;
  }

  registerUser(user: RegisterUser): Observable<any> {
    return this.http.post<RegisterUser>(
        environment.apiHost + 'v1/register',
        user
    );

}

}
