import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Booking } from '../../shared/models/booking/booking.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../services/booking/booking.service';
import { CreateBookingDTO } from '../../shared/dtos/createBookingDTO';
import { AuthService } from '../../services/auth/auth.service';
import { ClientDTO } from '../../shared/dtos/clientDto';

@Component({
  selector: 'app-booking-modal',
  templateUrl: './booking-modal.component.html',
  styleUrl: './booking-modal.component.css',
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class BookingModalComponent {
  @Input() booking!: Booking
  constructor(public activeModal: NgbActiveModal, private bookingService: BookingService, private authService: AuthService) {}

  selectedSeats: number = 0;
  confirmingPayment = false;
  endingPayment = false;
  selectedMilles: number = 0;

  paymentCode = "";

  hasPaymentError = false;
  paymentErrorMessage = "";

  confirmPayment(){
    this.confirmingPayment = true;
  }

  goBack(){
    this.confirmingPayment = false;
  }

  endPayment(){
    const bookingDTO = new CreateBookingDTO(
      (
        this.authService.getCurrentUserData() as ClientDTO).codigo,
        this.selectedSeats * this.booking.flight.valor_passagem,
        this.selectedMilles,
        this.selectedSeats,
        this.booking.flight.codigo,
        this.booking.flight.aeroporto_origem.codigo,
        this.booking.flight.aeroporto_destino.codigo
    )
    if (window.confirm('Você tem certeza que deseja finalizar a compra?')) {
      this.bookingService.create(bookingDTO).subscribe({
        next: (resp) => {
          console.log(resp)
          this.confirmingPayment = false;
          this.paymentCode = resp!.codigo
          this.endingPayment = true;
        },
        error: (er) => {
          console.log(er)
          this.hasPaymentError = true;
          this.paymentErrorMessage = er.error.message;
        }
      })
      
    }
  }

  validateSeats() {
    const max = this.booking.flight.quantidade_poltronas_total - this.booking.flight.quantidade_poltronas_ocupadas;
    if (this.selectedSeats > max) {
      this.selectedSeats = max;
    }
    if (this.selectedSeats < 1) {
      this.selectedSeats = 1;
    }
  }

  finish(){
    this.activeModal.close();
  }
}
