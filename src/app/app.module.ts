import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { CustomerHomeComponent } from './pages/customer-home/customer-home.component';
import { ViewBookingComponent } from './pages/view-booking/view-booking.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    CustomerHomeComponent,
    ViewBookingComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
