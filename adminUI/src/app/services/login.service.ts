import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  url = '/vehicles';


  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    })
  };

  constructor(private http: HttpClient) {
  }

  login(username: string, password: string) {
    localStorage.setItem('fleetName', username);
    localStorage.setItem('currentUser', window.btoa(username + ':' + password));
    return this.http.get<any>(this.url, this.httpOptions);
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    localStorage.removeItem('fleetName');
  }
}
