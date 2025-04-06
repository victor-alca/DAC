import { Component, OnInit } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';

@Component({
  selector: 'app-view-booking',
  templateUrl: './view-booking.component.html',
  styleUrls: ['./view-booking.component.css']
})
export class ViewBookingComponent implements OnInit {
  reservas: Booking[] = [];

  ngOnInit(): void {
    const flights: Flight[] = [
      new Flight('1', new Date('2024-03-22'), 'GRU', 'JFK', 1200, 200, 150, FlightStatus.CONFIRMED),
      new Flight('2', new Date('2024-04-20'), 'GIG', 'LIS', 1800, 180, 170, FlightStatus.REALIZED),
      new Flight('3', new Date('2024-05-10'), 'BSB', 'MIA', 1600, 220, 200, FlightStatus.CONFIRMED),
      new Flight('4', new Date('2024-06-15'), 'POA', 'MAD', 2500, 150, 120, FlightStatus.CANCELED),
    ];

    this.reservas = [
      new Booking(1, flights[0], new Date('2024-03-22'), BookingStatus.CREATED),
      new Booking(2, flights[1], new Date('2024-04-20'), BookingStatus.CHECK_IN),
      new Booking(3, flights[2], new Date('2024-05-10'), BookingStatus.CREATED),
      new Booking(4, flights[3], new Date('2024-06-15'), BookingStatus.CANCELED),
    ];
  }
}
