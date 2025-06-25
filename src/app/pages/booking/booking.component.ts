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
    if (!this.searchOrigin && !this.searchDestination) {
      console.log("Nenhum filtro fornecido, carregando todos os voos");
      this.loadFlights();
      return;
    }

    if (!this.searchOrigin || !this.searchDestination) {
      console.log("Apenas um campo preenchido, usando busca por período");
      const inicio = new Date();
      const fim = new Date();
      fim.setFullYear(fim.getFullYear() + 2);
      
      this.flightService.getAllByPeriod(inicio, fim).subscribe({
        next: (resp) => {
          console.log("Resposta da API período:", resp);
          if (resp && resp.voos && resp.voos.length > 0) {
            let voosFiltrados = resp.voos;
            
            if (this.searchOrigin) {
              const origemUpper = this.searchOrigin.toUpperCase();
              voosFiltrados = voosFiltrados.filter(v => 
                v.aeroporto_origem.codigo.includes(origemUpper) || 
                v.aeroporto_origem.nome.toUpperCase().includes(origemUpper)
              );
            }
            
            if (this.searchDestination) {
              const destinoUpper = this.searchDestination.toUpperCase();
              voosFiltrados = voosFiltrados.filter(v => 
                v.aeroporto_destino.codigo.includes(destinoUpper) || 
                v.aeroporto_destino.nome.toUpperCase().includes(destinoUpper)
              );
            }
            
            this.flights = voosFiltrados;
            console.log("Voos filtrados:", this.flights.length);
          } else {
            this.flights = [];
          }
        },
        error: (e) => {
          console.error("Erro na pesquisa por período:", e);
          this.flights = [];
        }
      });
      return;
    }

    const agora = new Date();
    const dataVooStr = agora.toISOString();
    
    const params = {
      data: dataVooStr,
      origem: this.searchOrigin.toUpperCase(),
      destino: this.searchDestination.toUpperCase()
    };

    console.log("Buscando com ambos os parâmetros:", params);

    this.flightService.getFlightsByParams(params).subscribe({
      next: (resp) => {
        console.log("Resposta da API específica:", resp);
        if (resp && resp.voos && resp.voos.length > 0) {
          this.flights = resp.voos; 
          console.log("Voos encontrados:", this.flights.length);
        } else {
          console.log("Nenhum voo encontrado para os critérios específicos");
          this.flights = []; 
        }
      },
      error: (e) => {
        console.error("Erro na pesquisa específica:", e);
        this.flights = [];
      }
    });
  }

  clearFilters() {
    this.searchOrigin = '';
    this.searchDestination = '';
    this.loadFlights();
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