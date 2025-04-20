import { Component } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingService } from '../../services/booking.service';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { FlightService } from '../../services/flight.service';

@Component({
  selector: 'app-booking-lookup',
  templateUrl: './booking-lookup.component.html',
  styleUrl: './booking-lookup.component.css'
})
export class BookingLookupComponent {
  bookingId: string = '';
  reserva: Booking | null = null;
  errorMessage: string | null = null;

  constructor(private bookingService: BookingService, private flightService: FlightService) {}

  onSubmit(): void {
    this.errorMessage = null;
    this.reserva = null;

    if (!this.bookingId.trim()) {
      this.errorMessage = 'Por favor, insira um código de reserva válido.';
      return;
    }

    const id = parseInt(this.bookingId, 10);
    const booking = this.bookingService.getById(id);

    if (!booking) {
      this.errorMessage = 'Reserva não encontrada.';
      return;
    }

    this.reserva = booking;
  }

  canCheckIn(): boolean {
    if (!this.reserva) return false;
    const now = new Date();
    const flightDate = new Date(this.reserva.flight.date);
    const diffInHours = (flightDate.getTime() - now.getTime()) / (1000 * 60 * 60);
    return diffInHours <= 48 && diffInHours > 0 && this.reserva.status === BookingStatus.CREATED;
  }

  onCheckIn(): void {
    if (this.reserva) {
      this.reserva.status = BookingStatus.CHECK_IN;
      this.bookingService.update(this.reserva);
      alert('Check-In realizado com sucesso!');
    }
  }

  onCancel(): void {
    if (this.reserva) {
      const confirmCancel = confirm('Tem certeza de que deseja cancelar esta reserva?');
      if (confirmCancel) {
        this.bookingService.delete(this.reserva.ID);
        this.reserva = null;
        alert('Reserva cancelada com sucesso!');
      }
    }
  }

  getBookingStatusText(status: BookingStatus): string {
    return this.bookingService.getBookingStatusText(status);
  }

  getFlightStatusText(status: FlightStatus): string {
    return this.flightService.getFlightStatusText(status);
  }

}