import { Component, OnInit } from '@angular/core';
import { FlightService } from '../../services/flight/flight.service';
import { BookingService } from '../../services/booking/booking.service';
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
getFlightStatusNumber(status: FlightStatus) {
return this.flightService.getFlightStatusNumber(status)
}
  flights: Flight[]  = [];
  errorMessage: string | null = null;
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
          // Filtra apenas voos confirmados (não cancelados nem realizados)
          this.flights = resp.voos.filter(flight => 
            this.flightService.getFlightStatusNumber(flight.estado) === FlightStatus.CONFIRMED
          );
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
    if (this.flightService.getFlightStatusNumber(flight.estado) === FlightStatus.CONFIRMED) {
      const confirmation = confirm(`Tem certeza que deseja cancelar o voo ${flight.aeroporto_origem.codigo} -> ${flight.aeroporto_destino.codigo}?`);
      if (!confirmation) {
        return;
      }
      this.flightService.updateFlightStatus(flight.codigo, "CANCELADO").subscribe({
      next: (response: any) => {
        if (response) {
          // Atualiza os dados da reserva com a resposta
      console.log(`Voo cancelado: ${flight.aeroporto_origem.codigo} -> ${flight.aeroporto_destino.codigo}`);
          alert(`Voo ${flight.aeroporto_origem.codigo} -> ${flight.aeroporto_destino.codigo} cancelado com sucesso!`);
        }
      },
      error: (error) => {
        console.error('Erro ao cancelar voo:', error);
        if (error.status === 403) {
          this.errorMessage = 'Você não tem permissão para este voo.';
        } else if (error.status === 400) {
          this.errorMessage = 'Não é possível cancelar este voo no momento.';
        }
      }
    });

      this.buscarVoos()
    }
  }

  markAsCompleted(flight: Flight) {
    if (this.flightService.getFlightStatusNumber(flight.estado) === FlightStatus.CONFIRMED) {
      const confirmation = confirm(`Tem certeza que deseja marcar o voo ${flight.aeroporto_origem.codigo} -> ${flight.aeroporto_destino.codigo} como realizado?`);
      if (!confirmation) {
        return;
      }

      this.flightService.updateFlightStatus(flight.codigo, "REALIZADO").subscribe({
      next: (response: any) => {
        if (response) {
          // Atualiza os dados da reserva com a resposta
      console.log(`Voo realizado: ${flight.aeroporto_origem.codigo} -> ${flight.aeroporto_destino.codigo}`);
          alert(`Voo ${flight.aeroporto_origem.codigo} -> ${flight.aeroporto_destino.codigo} marcado como realizado com sucesso!`);
        }
      },
      error: (error) => {
        console.error('Erro ao marcar voo como realizado:', error);
        if (error.status === 403) {
          this.errorMessage = 'Você não tem permissão marcar este voo como realizado.';
        } else if (error.status === 400) {
          this.errorMessage = 'Não é possível marcar este voo como realizado no momento.';
        }
      }
    });


      this.buscarVoos()
    }
  }
}
