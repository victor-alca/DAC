import { Component } from '@angular/core';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';

@Component({
  selector: 'app-boarding-confirmation',
  templateUrl: './boarding-confirmation.component.html',
  styleUrls: ['./boarding-confirmation.component.css']
})
export class BoardingConfirmationComponent {
  reservationCode: string = '';
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private bookingService: BookingService) {}

  confirmBoarding(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (!this.reservationCode.trim()) {
      this.errorMessage = 'Por favor, insira um código de reserva válido.';
      return;
    }

    const bookingId = parseInt(this.reservationCode, 10);
    const booking = this.bookingService.getById(bookingId);

    if (!booking) {
      this.errorMessage = 'Código de reserva inválido ou não encontrado.';
      return;
    }

    if (booking.status !== BookingStatus.CHECK_IN) {
      this.errorMessage = 'A reserva não está no estado CHECK-IN.';
      return;
    }

    if (confirm(`Tem certeza que deseja confirmar o embarque da reserva ${this.reservationCode}?`)) {
      booking.status = BookingStatus.SHIPPED; // Estado EMBARCADO
      this.bookingService.update(booking);
      this.successMessage = `Reserva ${this.reservationCode} confirmada com sucesso!`;
      this.reservationCode = ''; // Limpa o campo após sucesso
    }
  }
}
