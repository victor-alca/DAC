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
import { provideNgxMask } from 'ngx-mask';
import { ExtractComponent } from './pages/extract/extract.component';

@NgModule({
  declarations: [
    AppComponent,
    EmployeeDashboardComponent,
    BoardingConfirmationComponent,
    CustomerHomeComponent,
    ViewBookingComponent,
    ExtractComponent
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
    FormsModule
  ],
  providers: [provideNgxMask()],
  bootstrap: [AppComponent]
})
export class AppModule { }
