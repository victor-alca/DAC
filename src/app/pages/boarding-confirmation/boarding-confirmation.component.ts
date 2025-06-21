import { Component } from '@angular/core';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { CreateBookingDTO } from '../../shared/dtos/createBookingDTO';
import { firstValueFrom } from 'rxjs';
import { CreateBookingResponseDTO } from '../../shared/dtos/createBookingResponseDTO';

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

  async confirmBoarding(): Promise<void> {
    this.errorMessage = null;
    this.successMessage = null;

    if (!this.reservationCode.trim()) {
      this.errorMessage = 'Por favor, insira um código de reserva válido.';
      return;
    }

    const booking : CreateBookingResponseDTO = await this.loadBooking()


    if (!booking) {
      this.errorMessage = 'Código de reserva inválido ou não encontrado.';
      return;
    }

    if (booking.estado !== this.bookingService.getBookingStatusText(BookingStatus.CHECK_IN)) {
      this.errorMessage = 'A reserva não está no estado CHECK-IN.';
      return;
    }

    if (confirm(`Tem certeza que deseja confirmar o embarque da reserva ${this.reservationCode}?`)) {
      booking.estado = this.bookingService.getBookingStatusText(BookingStatus.SHIPPED); // Estado EMBARCADO
      this.bookingService.update(booking);
      this.successMessage = `Reserva ${this.reservationCode} confirmada com sucesso!`;
      this.reservationCode = ''; // Limpa o campo após sucesso
    }
  }

    async loadBooking(): Promise<CreateBookingResponseDTO> {
       try {
      const resp = await firstValueFrom(this.bookingService.getById(this.reservationCode));
  
      if (resp != null) {
        return resp
      } else {
        console.log("Nenhuma reserva encontrada");
        throw new Error("Nenhuma reserva encontrada")
      }
  
    } catch (e) {
      console.log("Erro ao buscar reservas:", e);
      throw (e)
    }
    }
}
