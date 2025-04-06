import { Component, OnInit } from '@angular/core';
import { FlightService } from '../../services/flight.service';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';

@Component({
  selector: 'app-flight-registration',
  templateUrl: './flight-registration.component.html',
  styleUrls: ['./flight-registration.component.css']
})
export class FlightRegistrationComponent implements OnInit {
  flightCode: string = '';
  date: Date | null = null;
  originAirport: string = '';
  destinationAirport: string = '';
  ticketCost: string = ''; // Usar string para aplicar a máscara
  totalSeats: number | null = null;
  successMessage: string | null = null;
  airports: string[] = []; // Lista de aeroportos
  milesEquivalent: number | null = null; // Quantidade de milhas equivalente ao valor da passagem

  constructor(private flightService: FlightService) {}

  ngOnInit(): void {
    this.loadAirports();
  }

  loadAirports(): void {
    const storedAirports = localStorage.getItem('airports');
    if (storedAirports) {
      this.airports = JSON.parse(storedAirports);
    } else {
      this.seedAirports();
    }
  }

  seedAirports(): void {
    const airports = ['GRU', 'JFK', 'CWB', 'GIG', 'BSB', 'POA', 'REC', 'FOR', 'LIS', 'MIA'];
    localStorage.setItem('airports', JSON.stringify(airports));
    this.airports = airports;
  }

  updateMilesEquivalent(): void {
    const numericTicketCost = parseFloat(String(this.ticketCost).replace(/[^\d.-]/g, '')); // Remove máscara e converte para número
    if (!isNaN(numericTicketCost)) {
      this.milesEquivalent = Math.floor(numericTicketCost / 5); // 1 milha a cada R$ 5,00
    } else {
      this.milesEquivalent = null;
    }
  }

  registerFlight(): void {
    if (!this.date || !this.originAirport || !this.destinationAirport || !this.ticketCost || !this.totalSeats) {
      alert('Por favor, preencha todos os campos.');
      return;
    }
  
    const flightDate = new Date(this.date); // Converte string em Date

    const currentDate = new Date();
    const maxFutureDate = new Date();
    maxFutureDate.setFullYear(currentDate.getFullYear() + 1);
    
    // Verificar se a data é passada ou muito futura
    if (flightDate < currentDate) {
      alert('A data do voo não pode ser no passado.');
      return;
    }
    
    if (flightDate > maxFutureDate) {
      alert('A data do voo não pode ser superior a 1 ano no futuro.');
      return;
    }

    if (this.originAirport === this.destinationAirport) {
      alert('O aeroporto de origem e destino não podem ser iguais.');
      return;
    }
  
    // Garantir que ticketCost é uma string antes de usar replace
    const numericTicketCost = parseFloat(String(this.ticketCost).replace(/[^\d.-]/g, '')); // Remove máscara e converte para número
  
    const newFlight = new Flight(
      '', // ID será gerado automaticamente
      this.date,
      this.originAirport,
      this.destinationAirport,
      numericTicketCost,
      this.totalSeats,
      0, // Assentos ocupados inicialmente
      FlightStatus.CONFIRMED
    );
  
    const generatedId = this.flightService.create(newFlight); // Recebe o ID gerado
    this.successMessage = `Voo ${generatedId} cadastrado com sucesso!`; // Exibe o ID na mensagem
    this.resetForm();
  }

  resetForm(): void {
    this.flightCode = '';
    this.date = null;
    this.originAirport = '';
    this.destinationAirport = '';
    this.ticketCost = '';
    this.totalSeats = null;
    this.milesEquivalent = null; // Reseta o cálculo de milhas
  }

  allowOnlyNumbers(event: KeyboardEvent): void {
    const inputElement = event.target as HTMLInputElement;
    const charCode = event.key.charCodeAt(0);
  
    // Permitir apenas números (0-9)
    if (charCode < 48 || charCode > 57) {
      event.preventDefault();
      return;
    }
  
    // Impedir que o número comece com '0'
    if (inputElement.value.length === 0 && charCode === 48) {
      event.preventDefault();
    }
  }
  
}
