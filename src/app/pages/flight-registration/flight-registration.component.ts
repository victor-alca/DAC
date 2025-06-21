import { Component, OnInit } from '@angular/core';
import { FlightService } from '../../services/flight.service';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { Airport } from '../../shared/models/airport/airport.model';
import { CreateFlightDTO } from '../../shared/dtos/createFlightDTO';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-flight-registration',
  templateUrl: './flight-registration.component.html',
  styleUrls: ['./flight-registration.component.css']
})
export class FlightRegistrationComponent implements OnInit {
  flightCode: string = '';
  date: Date | null = null;
  originAirport: Airport = new Airport("", "", "", "");
  destinationAirport: Airport = new Airport("", "", "", "");
  ticketCost: string = ''; // Usar string para aplicar a máscara
  totalSeats: number | null = null;
  successMessage: string | null = null;
  airports: Airport[] = []; // Lista de aeroportos
  milesEquivalent: number | null = null; // Quantidade de milhas equivalente ao valor da passagem

  constructor(private flightService: FlightService) {}

  async ngOnInit(): Promise<void> {
    await this.loadAirports();
    console.log(this.airports)
  }

  async loadAirports(): Promise<void> {
     try {
    const resp = await firstValueFrom(this.flightService.getAirports());

    if (resp != null) {
      this.airports = resp;
      this.originAirport = this.airports[0]
      this.destinationAirport = this.airports[1]
      console.log("Dados recebidos:", this.airports);
    } else {
      console.log("Nenhum aeroporto cadastrado");
      this.airports = [];
    }

  } catch (e) {
    console.log("Erro ao buscar aeroportos:", e);
    this.airports = [];
  }
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

    console.log(this.originAirport)
    console.log(this.destinationAirport)
    if (this.originAirport === this.destinationAirport) {
      alert('O aeroporto de origem e destino não podem ser iguais.');
      return;
    }
  
    // Garantir que ticketCost é uma string antes de usar replace
    const numericTicketCost = parseFloat(String(this.ticketCost).replace(/[^\d.-]/g, '')); // Remove máscara e converte para número
  
    const flightDTO = new CreateFlightDTO(
      this.date.toString(),
      numericTicketCost,
      this.totalSeats,
      0, // Assentos ocupados inicialmente
      this.originAirport.codigo,
      this.destinationAirport.codigo,
    );

    console.log(flightDTO)


    this.flightService.create(flightDTO).subscribe({
      next: (resp) => {
          console.log(resp)
          this.successMessage = `Voo ${1} cadastrado com sucesso!`; // Exibe o ID na mensagem

        },
        error: (er) => {
          console.log(er)
        }
    })
  
    // const generatedId = this.flightService.create(); // Recebe o ID gerado
    this.resetForm();
  }

  resetForm(): void {
    this.flightCode = '';
    this.date = null;
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
