import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { FlightService } from '../../services/flight/flight.service';
import { Flight } from '../../shared/models/flight/flight.model';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class BookingComponent {
  
  flights: Flight[] = [];
  searchOrigin: string = '';
  searchDestination: string = '';

  constructor(private modalService: NgbModal, private flightService: FlightService) {}

  ngOnInit() {
    this.loadFlights();
  }

  loadFlights() {
    const dataFim = new Date();
    dataFim.setFullYear(dataFim.getFullYear() + 2);
    this.flightService.getAllByPeriod(new Date(), dataFim).subscribe({
      next: (resp) => {
        if (resp != null) {
          this.flights = resp.voos;
        } else {
          console.log("nenhum voo cadastrado")
          this.flights = []; 
        }
      },
      error: (e) => {
        console.log(e)
        this.flights = [];
      }
    });
  }

  loadFlightsWithFilter(){
    this.flightService.getAllByOriginAndDestiny(new Date(), this.searchOrigin, this.searchDestination).subscribe({
      next: (resp) => {
        if (resp != null) {
          this.flights = resp.voos; 
        } else {
          console.log("Nenhum voo cadastrado")
          this.flights = []; 
        }
      },
      error: (e) => {
        console.log(e)
        this.flights = [];
      }
    });
  }

  openBookingModal(flightCode: string) {
    const modalRef = this.modalService.open(BookingModalComponent);
    const selectedFlight = this.flights.find(f => f.codigo == flightCode);
    modalRef.componentInstance.booking = new Booking(
      1,
      selectedFlight!,
      new Date(),
      BookingStatus.CREATED,
      0,
      0
    );

    modalRef.result.then(
      (result) => {
        this.loadFlights();
      }
    )
  }
  
}