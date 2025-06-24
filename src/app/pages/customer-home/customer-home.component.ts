import { Component, OnInit } from '@angular/core';
import { Booking } from '../../shared/models/booking/booking.model';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { BookingService } from '../../services/booking/booking.service';
import { Router } from '@angular/router';
import { ClientService } from '../../services/client/client.service';
import { AuthService } from '../../services/auth/auth.service';
import { ClientDTO } from '../../shared/dtos/clientDto';
import { CreateBookingResponseDTO } from '../../shared/dtos/createBookingResponseDTO';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfimationModalComponent } from '../confimation-modal/confimation-modal.component';

@Component({
  selector: 'app-customer-home',
  templateUrl: './customer-home.component.html',
  styleUrls: ['./customer-home.component.css']
})
export class CustomerHomeComponent implements OnInit {
  reservas: CreateBookingResponseDTO[] = [];
  reservasReservadas: CreateBookingResponseDTO[] = [];
  reservasFeitas: CreateBookingResponseDTO[] = [];
  reservasCanceladas: CreateBookingResponseDTO[] = [];
  milhas: number = 0
  clientCode: number | null = null
  
  constructor(private modalService: NgbModal, private bookingService: BookingService, private authService: AuthService, private clientService: ClientService, private router: Router) {}

  ngOnInit(): void {
    let currentClient = this.authService.getCurrentUserData() as ClientDTO
    this.clientCode = currentClient.codigo
    this.getBookings()
    this.clientService.getById(currentClient.codigo).subscribe((resp) => {
      console.log(resp!.saldo_milhas)
      this.milhas = resp!.saldo_milhas
    })
  }

  getBookings(){
    this.clientService.getClientBookings(this.clientCode!).subscribe({
      next: (resp) => {
        this.reservas = resp!
        if(this.reservas){
          this.filterBookings();
        }        
      },
      error: (e) => {
        console.log(e)
      }
    });
  }

  filterBookings(): void {
    this.reservasReservadas = this.reservas.filter(
      (reserva) => reserva.estado == "CRIADA"
    );
    this.reservasFeitas = this.reservas.filter(
      (reserva) => reserva.estado == "REALIZADA"
    );
    this.reservasCanceladas = this.reservas.filter(
      (reserva) => reserva.estado == "CANCELADA"
    );
  }

  getBookingStatusText(status: BookingStatus): string {
    return this.bookingService.getBookingStatusText(status);
  }

  cancelBooking(codigo: string): void {
    const modal = this.openModal("Cancelar reserva.", "Tem certeza que deseja cancelar a reserva?");
    modal.result.then(
      (confirm) => {
        this.bookingService.delete(codigo).subscribe({
          next: (resp) => {
            alert("Reserva cancelada com sucesso!")
            this.getBookings()
          },
          error: (er) => {
            console.log(er);
          }
        });
      },
      (dismiss) => {
        return;
      }
    );
  }

  openModal(text: string, extraText: string) {
    const modalRef = this.modalService.open(ConfimationModalComponent);
    modalRef.componentInstance.text = text;
    modalRef.componentInstance.extraText = extraText;
    return modalRef;
  }
  
}
