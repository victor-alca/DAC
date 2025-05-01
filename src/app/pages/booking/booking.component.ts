import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class BookingComponent {
  
  bookings: Booking[] = [];
  filteredBookings: Booking[] = [];
  searchOrigin: string = '';
  searchDestination: string = '';

  constructor(private modalService: NgbModal, private bookingService: BookingService) {}

  ngOnInit() {
    this.loadBookings();
    this.filterTable();
  }

  loadBookings() {
    this.bookings = this.bookingService.getAll();
    this.filteredBookings = [...this.bookings];
  }

  filterTable() {
    this.filteredBookings = this.bookings
      .filter(b => 
        b.flight.originAirport.toLowerCase().includes(this.searchOrigin.toLowerCase()) &&
        b.flight.destinationAirport.toLowerCase().includes(this.searchDestination.toLowerCase())
      )
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
  }

  openBookingModal() {
    const modalRef = this.modalService.open(BookingModalComponent);
    modalRef.componentInstance.booking = new Booking(
      1,
      this.bookings[0]?.flight, // Exemplo: usa o primeiro voo da lista
      new Date(),
      BookingStatus.CREATED,
      0,
      0
    );
  }
}