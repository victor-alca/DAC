import { Component } from '@angular/core';
import { Flight } from '../../shared/models/flight/flight.model';
import { Booking } from '../../shared/models/booking/booking.model';

@Component({
  selector: 'app-booking-lookup',
  templateUrl: './booking-lookup.component.html',
  styleUrl: './booking-lookup.component.css'
})
export class BookingLookupComponent {
  onSubmit(formValue: any){
    console.log(formValue)
  }
  voo = new Flight('1', new Date, "JFK", "CWB", 7777.7, 48, 20, 1)
  reserva = new Booking(1,this.voo,new Date(), 1);
}
