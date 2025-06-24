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

  confirmBoarding(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (!this.reservationCode.trim()) {
      this.errorMessage = 'Por favor, insira um código de reserva válido.';
      return;
    }

    let booking : CreateBookingResponseDTO|null = null

    this.bookingService.getById(this.reservationCode).subscribe(
        {
          next: (resp) => {
      if (resp != null) {
        booking = resp
        

      if (booking.estado !== "CHECK-IN") {
        this.errorMessage = 'A reserva não está no estado CHECK-IN.';
        return;
      }

      if (confirm(`Tem certeza que deseja confirmar o embarque da reserva ${this.reservationCode}?`)) {
        this.bookingService.updateBookingStatus(booking.codigo, "EMBARCADA")// Estado EMBARCADO
        .subscribe({
      next: (response: any) => {
        if (response) {
          // Atualiza os dados da reserva com a resposta
          alert('Embarque realizado com sucesso!');
        }
      },
      error: (error) => {
        console.error('Erro ao fazer embarque:', error);
        if (error.status === 403) {
          this.errorMessage = 'Você não tem permissão para fazer o embarque desta reserva.';
        } else if (error.status === 400) {
          this.errorMessage = 'Não é possível fazer o embarque desta reserva no momento.';
        }
      }
    });
        this.reservationCode = ''; // Limpa o campo após sucesso
      }


      } else {
        console.log("Nenhuma reserva encontrada");
        throw new Error("Nenhuma reserva encontrada")
      }
        },
        error: (er) => {
          console.log(er)
        }
        });



    
  }

}
