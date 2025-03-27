import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import * as pages from './pages';
import { TelaInicialClienteComponent } from './tela-inicial-cliente/tela-inicial-cliente.component';
import { VerReservaComponent } from './ver-reserva/ver-reserva.component';

const routes: Routes = [
  { path: '', component: pages.LoginComponent },
  { path: 'login', component: pages.LoginComponent },
  { path: 'tela-inicial-cliente', component: TelaInicialClienteComponent},
  { path: 'ver-reserva', component: VerReservaComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
