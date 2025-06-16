import { Component, OnInit } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { BookingService } from '../../services/booking.service';
import { Router } from '@angular/router';
import { ClientService } from '../../services/client/client.service';
import { AuthService } from '../../services/auth/auth.service';
import { ClientDTO } from '../../shared/models/sing/clientDto';

@Component({
  selector: 'app-customer-home',
  templateUrl: './customer-home.component.html',
  styleUrls: ['./customer-home.component.css']
})
export class CustomerHomeComponent implements OnInit {
  reservas: Booking[] = [];
  reservasReservadas: Booking[] = [];
  reservasFeitas: Booking[] = [];
  reservasCanceladas: Booking[] = [];
  milhas: Number = 0

  constructor(private bookingService: BookingService, private authService: AuthService, private clientService: ClientService, private router: Router) {}

  ngOnInit(): void {
    this.reservas = this.bookingService.getAll();
    this.filterReservas();
    let currentClient = this.authService.getCurrentUserData() as ClientDTO
    this.clientService.getById(currentClient.codigo).subscribe((resp) => {
      console.log(resp!.saldo_milhas)
      this.milhas = resp!.saldo_milhas
    })
  }
  filterReservas(): void {
    this.reservasReservadas = this.reservas.filter(
      (reserva) => reserva.status === BookingStatus.CREATED
    );
    this.reservasFeitas = this.reservas.filter(
      (reserva) => reserva.status === BookingStatus.REALIZED
    );
    this.reservasCanceladas = this.reservas.filter(
      (reserva) => reserva.status === BookingStatus.CANCELED
    );
  }

  getBookingStatusText(status: BookingStatus): string {
    return this.bookingService.getBookingStatusText(status);
  }

  cancelBooking(bookingId: number): void {
    const confirmCancel = confirm("Tem certeza que deseja cancelar esta reserva?");
    if (!confirmCancel) return;
  
    const booking = this.reservas.find((reserva) => reserva.ID === bookingId);
    if (booking) {
      booking.status = BookingStatus.CANCELED;
      this.bookingService.update(booking);
      this.reservas = this.bookingService.getAll();
      this.filterReservas();
    }
  }
  
}
