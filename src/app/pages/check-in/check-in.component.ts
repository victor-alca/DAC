import { Component } from '@angular/core';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { Airport } from '../../shared/models/airport/airport.model';

@Component({
  selector: 'app-check-in',
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.css',
})
export class CheckInComponent {
  flights: Flight[] = [
    {
      data: new Date(),
      codigo: '1',
      quantidade_poltronas_ocupadas: 280,
      aeroporto_destino: new Airport("qwe", "afefe", "adww", "wqfewf"),
      aeroporto_origem: new Airport("qwe", "afefe", "adww", "wqfewf"),
      estado: FlightStatus.CONFIRMED,
      valor_passagem: 5000,
      quantidade_poltronas_total: 300
    },
  ];

  successMessage: string | null = null;

  checkIn(flight: Flight): void {
    if (confirm(`Deseja realizar o check-in para o voo ${flight.aeroporto_origem} -> ${flight.aeroporto_destino}?`)) {
      // Simula o check-in (aqui você pode adicionar lógica para atualizar o estado no backend ou localStorage)
      this.successMessage = `Check-in realizado com sucesso para o voo ${flight.aeroporto_origem} -> ${flight.aeroporto_destino}.`;

      // Remove a mensagem de sucesso após 5 segundos
      setTimeout(() => {
        this.successMessage = null;
      }, 5000);
    }
  }
}