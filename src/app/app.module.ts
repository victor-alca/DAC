import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { TelaInicialClienteComponent } from './tela-inicial-cliente/tela-inicial-cliente.component';
import { VerReservaComponent } from './ver-reserva/ver-reserva.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    TelaInicialClienteComponent,
    VerReservaComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
