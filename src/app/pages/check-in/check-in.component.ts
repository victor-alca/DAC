import { Component } from '@angular/core';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';

@Component({
  selector: 'app-check-in',
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.css',
})
export class CheckInComponent {
  flights: Flight[] = [
    {
      date: new Date(),
      ID: ' aa',
      occupatedSeats: 280,
      destinationAirport: "GRU",
      originAirport: "LIS",
      status: FlightStatus.CONFIRMED,
      ticketCost: 5000,
      totalSeats: 300
    },
  ];
}
