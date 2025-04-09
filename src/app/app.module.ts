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
import { SignComponent } from './pages/sign/sign.component';
import { CepValidatorDirective } from './shared/directives/cep-validator.directive';
import { PhoneValidatorDirective } from './shared/directives/phone-validator.directive';
import { EmailValidatorDirective } from './shared/directives/email-validator.directive';
import { CpfValidatorDirective } from './shared/directives/cpf-validator.directive';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { BookingModalComponent } from './pages/booking-modal/booking-modal.component';
import { BuyMilesComponent } from './pages/buy-miles/buy-miles.component';
import { NgxMaskPipe, provideNgxMask, NgxMaskDirective } from 'ngx-mask';
import { CancelBookingComponent } from './pages/cancel-booking/cancel-booking.component';
import { BookingLookupComponent } from './pages/booking-lookup/booking-lookup.component';
import { FlightRegistrationComponent } from './pages/flight-registration/flight-registration.component';
import { EmployeesComponent } from './pages/employees/employees.component';
import { EmployeesModalComponent } from './pages/employees-modal/employees-modal.component';
import { ConfimationModalComponent } from './pages/confimation-modal/confimation-modal.component';

@NgModule({
  declarations: [
    AppComponent,
    EmployeeDashboardComponent,
    BoardingConfirmationComponent,
    CustomerHomeComponent,
    ViewBookingComponent,
    BuyMilesComponent,
    ViewBookingComponent,
    CancelBookingComponent,
    BookingLookupComponent,
    FlightRegistrationComponent,
    EmployeesComponent,
    EmployeesModalComponent,
    ConfimationModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LoginComponent,
    SignComponent,
    CpfValidatorDirective,
    EmailValidatorDirective,
    PhoneValidatorDirective,
    CepValidatorDirective,
    FormsModule,
    NgbModule,
    NgxMaskPipe,
    FormsModule,
    NgxMaskDirective,
  ],
  providers: [provideNgxMask()],
  bootstrap: [AppComponent]
})
export class AppModule { }
