import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Booking } from '../../shared/models/booking/booking.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-booking-modal',
  templateUrl: './booking-modal.component.html',
  styleUrl: './booking-modal.component.css',
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class BookingModalComponent {
  @Input() booking!: Booking
  constructor(public activeModal: NgbActiveModal) {}

  selectedSeats: number = 0;
  confirmingPayment = false;
  endingPayment = false;
  selectedMilles: number = 0;

  paymentCode = "";

  confirmPayment(){
    this.confirmingPayment = true;
    this.paymentCode = this.generateCode();
  }

  goBack(){
    this.confirmingPayment = false;
  }

  endPayment(){
    this.confirmingPayment = false;
    this.endingPayment = true;
  }

  finish(){
    this.activeModal.close();
  }

  generateCode(): string{
    const letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const numbers = "1234567890"

    let code = ""

    for(let i=0; i<3; i++){
      code += letters[Math.floor(Math.random() * letters.length)]
    }

    for(let i=0; i<3; i++){
      code += numbers[Math.floor(Math.random() * numbers.length)]
    }

    return code 
  }
}
