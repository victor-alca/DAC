import { Component } from '@angular/core';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';

@Component({
  selector: 'app-check-in',
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.css',
})
export class CheckInComponent {
  flights: Flight[] = [
    {
      date: new Date(),
      ID: '1',
      occupatedSeats: 280,
      destinationAirport: "GRU",
      originAirport: "LIS",
      status: FlightStatus.CONFIRMED,
      ticketCost: 5000,
      totalSeats: 300
    },
  ];

  successMessage: string | null = null;

  checkIn(flight: Flight): void {
    if (confirm(`Deseja realizar o check-in para o voo ${flight.originAirport} -> ${flight.destinationAirport}?`)) {
      // Simula o check-in (aqui você pode adicionar lógica para atualizar o estado no backend ou localStorage)
      this.successMessage = `Check-in realizado com sucesso para o voo ${flight.originAirport} -> ${flight.destinationAirport}.`;

      // Remove a mensagem de sucesso após 5 segundos
      setTimeout(() => {
        this.successMessage = null;
      }, 5000);
    }
  }
}