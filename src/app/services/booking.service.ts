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
  constructor() { }

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
    const now = new Date();

    const bookings: Booking[] = [
      // Reservas para o primeiro voo
      new Booking(
      1,
      this.getFlightDetails('1')!, // Busca o voo com ID '1'
      new Date(now.getTime() + 1 * 60 * 60 * 1000), // 1 hora a partir de agora
      BookingStatus.CREATED,
      150, // Dinheiro gasto
      500  // Milhas gastas
      ),
      new Booking(
      2,
      this.getFlightDetails('1')!, // Busca o voo com ID '1'
      new Date(now.getTime() + 2 * 60 * 60 * 1000), // 2 horas a partir de agora
      BookingStatus.CHECK_IN,
      200, // Dinheiro gasto
      600  // Milhas gastas
      ),
      // Reservas para o segundo voo
      new Booking(
      3,
      this.getFlightDetails('2')!, // Busca o voo com ID '2'
      new Date(now.getTime() + 12 * 60 * 60 * 1000), // 12 horas a partir de agora
      BookingStatus.CREATED,
      300, // Dinheiro gasto
      800  // Milhas gastas
      ),
      new Booking(
      4,
      this.getFlightDetails('2')!, // Busca o voo com ID '2'
      new Date(now.getTime() + 14 * 60 * 60 * 1000), // 14 horas a partir de agora
      BookingStatus.CHECK_IN,
      250, // Dinheiro gasto
      700  // Milhas gastas
      ),
      // Reservas para o terceiro voo
      new Booking(
      5,
      this.getFlightDetails('3')!, // Busca o voo com ID '3'
      new Date(now.getTime() + 72 * 60 * 60 * 1000), // 72 horas a partir de agora
      BookingStatus.CANCELED,
      400, // Dinheiro gasto
      1000  // Milhas gastas
      ),
      new Booking(
      6,
      this.getFlightDetails('3')!, // Busca o voo com ID '3'
      new Date(now.getTime() + 74 * 60 * 60 * 1000), // 74 horas a partir de agora
      BookingStatus.CREATED,
      350, // Dinheiro gasto
      900  // Milhas gastas
      ),
    ];

    localStorage.setItem('bookings', JSON.stringify(bookings));
  }

  getBookingStatusText(status: BookingStatus): string {
    switch (status) {
      case BookingStatus.CREATED:
        return 'Criada';
      case BookingStatus.CHECK_IN:
        return 'Check-In Realizado';
      case BookingStatus.CANCELED:
        return 'Cancelada';
      case BookingStatus.FLIGHT_CANCELED:
        return 'Voo Cancelado';
      case BookingStatus.SHIPPED:
        return 'Embarcado';
      case BookingStatus.REALIZED:
        return 'Realizada';
      case BookingStatus.NOT_REALIZED:
        return 'Não Realizada';
      default:
        return 'Desconhecido';
    }
  }
}
