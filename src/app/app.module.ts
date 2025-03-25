import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { CepValidatorDirective } from './shared/directives/cep-validator.directive';
import { PhoneValidatorDirective } from './shared/directives/phone-validator.directive';
import { EmailValidatorDirective } from './shared/directives/email-validator.directive';
import { CpfValidatorDirective } from './shared/directives/cpf-validator.directive';
import { SignComponent } from './pages/sign/sign.component';
import { provideNgxMask } from 'ngx-mask';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LoginComponent,
    SignComponent,
    CpfValidatorDirective,
    EmailValidatorDirective,
    PhoneValidatorDirective,
    CepValidatorDirective
  ],
  providers: [provideNgxMask()],
  bootstrap: [AppComponent]
})
export class AppModule { }
