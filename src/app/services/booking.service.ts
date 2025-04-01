import { Injectable } from '@angular/core';
import { Flight } from '../shared/models/flight/flight.model';
import { Booking } from '../shared/models/booking/booking.model';

// const para o local storage
const LS_KEY = 'bookings';

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  constructor() {}

  getAll(): Booking[] {
    const bookings = localStorage[LS_KEY];
    return bookings ? JSON.parse(bookings) : [];
  }

  create(booking: Booking): void {
    const bookings = this.getAll();

    booking.ID = new Date().getTime();
    bookings.push(booking);

    localStorage[LS_KEY] = JSON.stringify(bookings);
  }

  getById(id: number): Booking | undefined {
    const bookings = this.getAll();
    return bookings.find((booking) => booking.ID === id);
  }

  update(booking: Booking): void {
    const bookings = this.getAll();

    bookings.forEach((obj, index, objs) => {
      if (booking.ID === obj.ID) {
        objs[index] = booking;
      }
    });

    localStorage[LS_KEY] = JSON.stringify(bookings);
  }

  delete(id: number): void {
    let bookings = this.getAll();

    bookings = bookings.filter((booking) => booking.ID !== id);
    localStorage[LS_KEY] = JSON.stringify(bookings);
  }

  get ActiveBookings(): Booking[] {
    return this.getAll().filter(booking => booking.status === 1);
  }

  get lastFlights(): Booking[] {
    return this.getAll().filter(booking => booking.status === 2);
  }

  getFlightDetails(flightId: number): Flight | undefined {
    const flights: Flight[] = JSON.parse(localStorage.getItem('flights') || '[]');
    return flights.find(flight => flight.ID === flightId);
  }
}
