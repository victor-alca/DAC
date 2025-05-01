import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import * as pages from './pages';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: pages.LoginComponent },
  { path: 'sign', component: pages.SignComponent },
  { path: 'employee-dashboard', component: pages.EmployeeDashboardComponent },
  { path: 'boarding-confirmation', component: pages.BoardingConfirmationComponent },
  { path: 'customer-home', component: pages.CustomerHomeComponent },
  { path: 'view-booking/:id', component: pages.ViewBookingComponent },
  { path: 'booking', component: pages.BookingComponent },
  { path: 'buy-miles', component: pages.BuyMilesComponent },
  { path: 'cancel-booking', component: pages.CancelBookingComponent },
  { path: 'booking-lookup', component: pages.BookingLookupComponent },
  { path: 'flight-registration', component: pages.FlightRegistrationComponent },
  { path: 'employees', component: pages.EmployeesComponent },
  { path: 'check-in', component: pages.CheckInComponent },
  { path: 'extract', component: pages.ExtractComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
