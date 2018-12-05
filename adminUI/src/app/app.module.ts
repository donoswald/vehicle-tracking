import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FleetOverviewComponent} from './fleet-overview/fleet-overview.component';
import {LoginComponent} from './login/login.component';
import {BasicAuthInterceptor} from './interceptor/basic-auth.interceptor';

import {ReactiveFormsModule} from '@angular/forms';
import {FleetNameComponent} from './fleet-overview/fleet-name/fleet-name.component';
import {VehicleDetailComponent} from './vehicle-detail/vehicle-detail.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AgmCoreModule} from '@agm/core';
import {ChartsModule} from 'ng2-charts';
import {LoggingInterceptor} from './interceptor/logging-interceptor';


@NgModule({
  declarations: [
    AppComponent,
    FleetOverviewComponent,
    LoginComponent,
    FleetNameComponent,
    VehicleDetailComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    ChartsModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyCoGcLJLB58G-zS70d03W0FQn7DmxNv194'
    })
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: BasicAuthInterceptor, multi: true}, {
      provide: HTTP_INTERCEPTORS,
      useClass: LoggingInterceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
