import { Component } from '@angular/core';

@Component({
  selector: 'app-boarding-confirmation',
  templateUrl: './boarding-confirmation.component.html',
  styleUrls: ['./boarding-confirmation.component.css']
})
export class BoardingConfirmationComponent {
  reservationCode: string = '';
  errorMessage: string | null = null;
  successMessage: string | null = null;

  validReservations = ['ABC123', 'DEF456', 'GHI789']; // Simulando reservas válidas
  // Vai ser necessário um service para buscar as reservas válidas do voo

  confirmBoarding(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (!this.reservationCode.trim()) {
      this.errorMessage = 'Por favor, insira um código de reserva válido.';
      return;
    }

    if (!this.validReservations.includes(this.reservationCode.toUpperCase())) {
      this.errorMessage = 'Código de reserva inválido ou não encontrado.';
      return;
    }

    if (confirm(`Tem certeza que deseja confirmar o embarque da reserva ${this.reservationCode}?`)) {
      this.successMessage = `Reserva ${this.reservationCode} confirmada com sucesso!`;
      this.reservationCode = ''; // Limpa o campo após sucesso
    }
  }
}
