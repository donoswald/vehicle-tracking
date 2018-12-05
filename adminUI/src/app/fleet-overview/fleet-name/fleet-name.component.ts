import {Component, OnInit} from '@angular/core';
import {LoginService} from '../../services/login.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-fleet-name',
  templateUrl: './fleet-name.component.html',
  styleUrls: ['./fleet-name.component.css']
})
export class FleetNameComponent implements OnInit {

  name: string;

  constructor(private loginService: LoginService, private router: Router) {
  }

  ngOnInit() {
    this.name = localStorage.getItem('fleetName');
  }

  logout() {
    this.loginService.logout();
    this.router.navigate(['./login']);
  }

}
