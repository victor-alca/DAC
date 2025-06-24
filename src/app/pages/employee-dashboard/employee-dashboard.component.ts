import { Component, OnInit } from '@angular/core';
import { FlightService } from '../../services/flight.service';
import { BookingService } from '../../services/booking.service';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FlightsDTO } from '../../shared/dtos/flightDto';

@Component({
  selector: 'app-employee-dashboard',
  templateUrl: './employee-dashboard.component.html',
  styleUrls: ['./employee-dashboard.component.css']
})
export class EmployeeDashboardComponent implements OnInit {
  flights: Flight[]  = [];
  successMessage: string | null = null;

  constructor(private flightService: FlightService, private bookingService: BookingService) {}

  ngOnInit() {
    this.buscarVoos()

  }

  buscarVoos() {
        const now = new Date();
        const next48Hours = new Date();
        next48Hours.setHours(now.getHours() + 48);

     this.flightService.getAllByPeriod(now, next48Hours).subscribe({
      next: (resp) => {
          console.log(resp)
          if (resp != null) {
          this.flights = resp.voos;
        } else {
          console.log("nenhum voo cadastrado")
          this.flights = []; 
        }
        },
        error: (er) => {
          console.log(er)
        }
    })
  }

  cancelFlight(flight: Flight) {
    if (flight.estado === FlightStatus.CONFIRMED) {
      const confirmation = confirm(`Tem certeza que deseja cancelar o voo ${flight.aeroporto_origem} -> ${flight.aeroporto_destino}?`);
      if (!confirmation) {
        return;
      }

      flight.estado = FlightStatus.CANCELED;
      // this.flightService.update(flight);

      // Cancela todas as reservas associadas ao voo
      const bookings = this.bookingService.getAll().filter(booking => booking.flight.codigo === flight.codigo);
      bookings.forEach(booking => {
        booking.status = BookingStatus.FLIGHT_CANCELED;
        // this.bookingService.update(booking);
      });

      this.buscarVoos()
      this.successMessage = `Voo ${flight.aeroporto_origem} -> ${flight.aeroporto_destino} cancelado com sucesso!`;
      console.log(`Voo cancelado: ${flight.aeroporto_origem} -> ${flight.aeroporto_destino}`);
    }
  }

  markAsCompleted(flight: Flight) {
    if (flight.estado === FlightStatus.CONFIRMED) {
      const confirmation = confirm(`Tem certeza que deseja marcar o voo ${flight.aeroporto_origem} -> ${flight.aeroporto_destino} como realizado?`);
      if (!confirmation) {
        return;
      }

      flight.estado = FlightStatus.REALIZED;
      // this.flightService.update(flight);

      // Atualiza todas as reservas associadas ao voo
      const bookings = this.bookingService.getAll().filter(booking => booking.flight.codigo === flight.codigo);
      bookings.forEach(booking => {
        if (booking.status === BookingStatus.SHIPPED) {
          booking.status = BookingStatus.REALIZED; // Reserva realizada
        } else {
          booking.status = BookingStatus.NOT_REALIZED; // Reserva não realizada
        }
        // this.bookingService.update(booking);
      });

      this.buscarVoos()
      this.successMessage = `Voo ${flight.aeroporto_origem} -> ${flight.aeroporto_destino} marcado como realizado com sucesso!`;
      console.log(`Voo realizado: ${flight.aeroporto_origem} -> ${flight.aeroporto_destino}`);
    }
  }
}
