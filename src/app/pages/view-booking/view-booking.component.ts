import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-view-booking',
  templateUrl: './view-booking.component.html',
  styleUrls: ['./view-booking.component.css']
})
export class ViewBookingComponent implements OnInit {
  reserva: Booking | null = null;
  statusText: string = '';

  constructor(private route: ActivatedRoute, private bookingService: BookingService) {}

  ngOnInit(): void {
    const bookingId = Number(this.route.snapshot.paramMap.get('id'));
    if (bookingId) {
      this.reserva = this.bookingService.getById(bookingId) ?? null;
      if (this.reserva) {
        this.statusText = this.bookingService.getBookingStatusText(this.reserva.status);
      }
    }
  }
}