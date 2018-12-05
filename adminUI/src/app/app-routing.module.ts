import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AuthGuard} from './auth/guard';
import {FleetOverviewComponent} from './fleet-overview/fleet-overview.component';
import {LoginComponent} from './login/login.component';
import {VehicleDetailComponent} from './vehicle-detail/vehicle-detail.component';

const routes: Routes = [
  {path: '', component: FleetOverviewComponent, canActivate: [AuthGuard]},
  {path: 'vehicle/:vin', component: VehicleDetailComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
