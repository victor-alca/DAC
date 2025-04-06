import { Component } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { Flight } from '../../shared/models/flight/flight.model';

@Component({
  selector: 'app-cancel-booking',
  templateUrl: './cancel-booking.component.html',
  styleUrl: './cancel-booking.component.css',
})
export class CancelBookingComponent {
  voo = new Flight('1', new Date, "JFK", "CWB", 7777.7, 48, 20, 1)
  reserva = new Booking(1,this.voo,new Date(), 1);
}
