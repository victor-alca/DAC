import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingService } from '../../services/booking/booking.service';
import { CreateBookingResponseDTO } from '../../shared/dtos/createBookingResponseDTO';

@Component({
  selector: 'app-view-booking',
  templateUrl: './view-booking.component.html',
  styleUrls: ['./view-booking.component.css']
})
export class ViewBookingComponent implements OnInit {
  reserva: CreateBookingResponseDTO | null = null;
  statusText: string = '';

  constructor(private route: ActivatedRoute, private bookingService: BookingService) {}

  ngOnInit(): void {
    const bookingId = String(this.route.snapshot.paramMap.get('id'));
    if (bookingId) {
      this.bookingService.getById(bookingId).subscribe({
        next: (resp) => {
          this.reserva = resp!
        },
        error: (e) => {
          console.log(e)
        }
      });
    }
  }
}