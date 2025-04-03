import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import * as pages from './pages';

const routes: Routes = [
  { path: '', component: pages.LoginComponent },
  { path: 'login', component: pages.LoginComponent },
  { path: 'sign', component: pages.SignComponent},
  { path: 'employee-dashboard', component: pages.EmployeeDashboardComponent },
  { path: 'boarding-confirmation', component: pages.BoardingConfirmationComponent },
  { path: 'customer-home', component: pages.CustomerHomeComponent},
  { path: 'view-booking', component: pages.ViewBookingComponent},
  { path: 'booking', component: pages.BookingComponent},
  { path: 'buy-miles', component: pages.BuyMilesComponent}
  { path: 'cancel-booking', component: pages.CancelBookingComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
