import { Component, OnInit } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { BookingService } from '../../services/booking.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-home',
  templateUrl: './customer-home.component.html',
  styleUrls: ['./customer-home.component.css']
})
export class CustomerHomeComponent implements OnInit {
  reservas: Booking[] = [];
  reservasReservadas: Booking[] = [];
  reservasFeitas: Booking[] = [];
  reservasCanceladas: Booking[] = [];

  constructor(private bookingService: BookingService, private router: Router) {}

  ngOnInit(): void {
    this.reservas = this.bookingService.getAll();
    this.filterReservas();
  }
  filterReservas(): void {
    this.reservasReservadas = this.reservas.filter(
      (reserva) => reserva.status === BookingStatus.CREATED
    );
    this.reservasFeitas = this.reservas.filter(
      (reserva) => reserva.status === BookingStatus.REALIZED
    );
    this.reservasCanceladas = this.reservas.filter(
      (reserva) => reserva.status === BookingStatus.CANCELED
    );
  }

  getBookingStatusText(status: BookingStatus): string {
    return this.bookingService.getBookingStatusText(status);
  }

  cancelBooking(bookingId: number): void {
    const confirmCancel = confirm("Tem certeza que deseja cancelar esta reserva?");
    if (!confirmCancel) return;
  
    const booking = this.reservas.find((reserva) => reserva.ID === bookingId);
    if (booking) {
      booking.status = BookingStatus.CANCELED;
      this.bookingService.update(booking);
      this.reservas = this.bookingService.getAll();
      this.filterReservas();
    }
  }
  
}
