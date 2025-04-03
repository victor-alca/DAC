import { Component } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';

@Component({
  selector: 'app-customer-home-cliente',
  templateUrl: './customer-home.component.html',
  styleUrl: './customer-home.component.css'
})
export class CustomerHomeComponent {

    reservas: Booking[] = [
      { ID: 1, flightId: 1, date: new Date('2024-03-22'), status: 1 },
      { ID: 2, flightId: 2, date: new Date('2024-04-20'), status: 1 },
      { ID: 3, flightId: 3, date: new Date('2024-05-10'), status: 2 },
      { ID: 4, flightId: 4, date: new Date('2024-06-15'), status: 2 }
    ];
  
    flights: Flight[] = [
      new Flight(1, new Date('2024-03-22'), 'GRU', 'JFK', 1200, '200', '150', FlightStatus.CONFIRMED),
      new Flight(2, new Date('2024-04-20'), 'GIG', 'LIS', 1800, '180', '170', FlightStatus.REALIZED),
      new Flight(3, new Date('2024-05-10'), 'BSB', 'MIA', 1600, '220', '200', FlightStatus.CONFIRMED),
      new Flight(4, new Date('2024-06-15'), 'POA', 'MAD', 2500, '150', '120', FlightStatus.CANCELED)
    ];
  
    get ActiveBookings(): Booking[] {
      return this.reservas.filter(r => r.status === 1);
    }

    get lastFlights(): Booking[] {
      return this.reservas.filter(r => r.status === 2);
    }

    getFlightDetails(flightId: number): Flight | undefined {
      return this.flights.find(flight => flight.ID === flightId);
    }

}
