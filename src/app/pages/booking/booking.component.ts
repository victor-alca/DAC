import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { Booking } from '../../shared/models/booking/booking.model';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class BookingComponent {
  
  constructor(private modalService: NgbModal) {}

  ngOnInit(){
    this.filterTable();
  }

  bookings = [
    { id: 1, data: new Date(2025, 4, 1), origin: 'GRU', destination: 'JFK', price: 1800 },  
    { id: 2, data: new Date(2025, 3, 25), origin: 'CWB', destination: 'LIS', price: 1200 },  
    { id: 3, data: new Date(2025, 5, 5), origin: 'GRU', destination: 'MIA', price: 1500 },  
    { id: 4, data: new Date(2025, 3, 15), origin: 'BSB', destination: 'MAD', price: 1300 },  
    { id: 5, data: new Date(2025, 4, 20), origin: 'BSB', destination: 'CDG', price: 1600 },  
    { id: 6, data: new Date(2025, 2, 31), origin: 'VCP', destination: 'FCO', price: 1400 },  
    { id: 7, data: new Date(2025, 3, 10), origin: 'CWB', destination: 'EZE', price: 900 },  
    { id: 8, data: new Date(2025, 3, 5), origin: 'REC', destination: 'LHR', price: 1750 },  
    { id: 9, data: new Date(2025, 4, 10), origin: 'REC', destination: 'MEX', price: 1350 },  
    { id: 10, data: new Date(2025, 3, 20), origin: 'CWB', destination: 'SCL', price: 1100 }  
  ];


  filteredBookings = [...this.bookings];

  searchOrigin: string = '';
  searchDestination: string = '';

  filterTable() {
    this.filteredBookings = this.bookings
        .filter(b => 
            b.origin.toLowerCase().includes(this.searchOrigin.toLowerCase()) &&
            b.destination.toLowerCase().includes(this.searchDestination.toLowerCase())
        )
        .sort((a, b) => new Date(a.data).getTime() - new Date(b.data).getTime());
  }

  openBookingModal(){
    const modalRef = this.modalService.open(BookingModalComponent);
    modalRef.componentInstance.booking = new Booking(1, new Flight('1', new Date(), "CWB", "GRU", 1200.99, 60, 49, FlightStatus.CONFIRMED), new Date(), BookingStatus.CREATED)
  }

}
