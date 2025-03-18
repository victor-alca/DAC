import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import * as pages from './pages';

const routes: Routes = [
  { path: '', component: pages.LoginComponent },
  { path: 'login', component: pages.LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
