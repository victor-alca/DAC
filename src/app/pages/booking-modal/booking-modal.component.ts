import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxMaskPipe } from 'ngx-mask';
import { Booking } from '../../shared/models/booking/booking.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-booking-modal',
  templateUrl: './booking-modal.component.html',
  styleUrl: './booking-modal.component.css',
  standalone: true,
  imports: [NgxMaskPipe, FormsModule]
})
export class BookingModalComponent {
  @Input() booking!: Booking
  constructor(public activeModal: NgbActiveModal) {}

  selectedSeats: number = 0;
}
