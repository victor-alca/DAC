import { Injectable } from '@angular/core';
import { Booking } from '../shared/models/booking/booking.model';
import { BookingStatus } from '../shared/models/booking/booking-status.enum';
import { Flight } from '../shared/models/flight/flight.model';
import { FlightStatus } from '../shared/models/flight/flight-status.enum';

// const para o local storage
const LS_KEY = 'bookings';

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  constructor() {}

  getAll(): Booking[] {
    const bookings = localStorage[LS_KEY];
    return bookings ? JSON.parse(bookings).map((booking: any) => ({
      ...booking,
      date: new Date(booking.date), // Converte strings de data para objetos Date
      flight: {
        ...booking.flight,
        date: new Date(booking.flight.date) // Converte strings de data para objetos Date no voo
      }
    })) : [];
  }

  create(booking: Booking): void {
    const bookings = this.getAll();

    booking.ID = new Date().getTime();
    bookings.push(booking);

    localStorage[LS_KEY] = JSON.stringify(bookings);
  }

  getById(id: number): Booking | undefined {
    return this.getAll().find((booking) => booking.ID === id);
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

  getFlightDetails(flightId: string): Flight | undefined {
    const flights: Flight[] = JSON.parse(localStorage.getItem('flights') || '[]');
    return flights.find(flight => flight.ID === flightId);
  }
  // Método temporário para inserir reservas manualmente
  seedBookings(): void {
    const flights: Flight[] = [
      new Flight('1', new Date('2025-04-06T12:10:03.087Z'), 'CWB', 'GRU', 500, 180, 50, FlightStatus.CONFIRMED),
      new Flight('2', new Date('2025-04-06T00:10:03.087Z'), 'GRU', 'GIG', 400, 200, 120, FlightStatus.CONFIRMED),
      new Flight('3', new Date('2025-04-06T11:10:03.087Z'), 'BSB', 'POA', 300, 150, 80, FlightStatus.CANCELED),
      new Flight('4', new Date('2025-04-05T12:10:03.087Z'), 'REC', 'FOR', 600, 100, 60, FlightStatus.REALIZED),
    ];

    const bookings: Booking[] = [
      new Booking(1, flights[0], new Date('2025-03-29T10:00:00'), BookingStatus.CREATED),
      new Booking(2, flights[0], new Date('2025-03-29T10:00:00'), BookingStatus.CHECK_IN),
      new Booking(3, flights[1], new Date('2025-03-29T14:30:00'), BookingStatus.CREATED),
      new Booking(4, flights[2], new Date('2025-03-29T08:00:00'), BookingStatus.CANCELED),
    ];
    localStorage[LS_KEY] = JSON.stringify(bookings);
  }
}
