import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { EmployeeDashboardComponent } from './pages/employee-dashboard/employee-dashboard.component';
import { BoardingConfirmationComponent } from './pages/boarding-confirmation/boarding-confirmation.component';
import { FormsModule } from '@angular/forms';
import { CustomerHomeComponent } from './pages/customer-home/customer-home.component';
import { ViewBookingComponent } from './pages/view-booking/view-booking.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    EmployeeDashboardComponent,
    BoardingConfirmationComponent,
    CustomerHomeComponent,
    ViewBookingComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
