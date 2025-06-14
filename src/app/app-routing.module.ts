import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import * as pages from './pages';
import { authGuard } from './services/auth/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: pages.LoginComponent },
  { path: 'sign', component: pages.SignComponent },
  { path: 'employee-dashboard', component: pages.EmployeeDashboardComponent, canActivate: [authGuard], data: { userType: 'FUNCIONARIO' } },
  { path: 'boarding-confirmation', component: pages.BoardingConfirmationComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'customer-home', component: pages.CustomerHomeComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'view-booking/:id', component: pages.ViewBookingComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'booking', component: pages.BookingComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'buy-miles', component: pages.BuyMilesComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'cancel-booking', component: pages.CancelBookingComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'booking-lookup', component: pages.BookingLookupComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'flight-registration', component: pages.FlightRegistrationComponent, canActivate: [authGuard], data: { userType: 'FUNCIONARIO' } },
  { path: 'employees', component: pages.EmployeesComponent, canActivate: [authGuard], data: { userType: 'FUNCIONARIO' } },
  { path: 'check-in', component: pages.CheckInComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
  { path: 'extract', component: pages.ExtractComponent, canActivate: [authGuard], data: { userType: 'CLIENTE' } },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: false})],
  exports: [RouterModule],
})
export class AppRoutingModule {}
