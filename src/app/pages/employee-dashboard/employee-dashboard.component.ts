import { Component, OnInit } from '@angular/core';
import { FlightService } from '../../services/flight.service';
import { BookingService } from '../../services/booking.service';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { BookingStatus } from '../../shared/models/booking/booking-status.enum';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-employee-dashboard',
  templateUrl: './employee-dashboard.component.html',
  styleUrls: ['./employee-dashboard.component.css']
})
export class EmployeeDashboardComponent implements OnInit {
  flights: Flight[] = [];
  successMessage: string | null = null;

  constructor(private flightService: FlightService, private bookingService: BookingService) {}

  ngOnInit() {
    // Limpa o localStorage (remover em produção)
    // localStorage.removeItem('flights');
    // localStorage.removeItem('bookings');
    // Seed de dados temporários para testes (remover em produção)
    // this.flightService.seedFlights();
    // this.bookingService.seedBookings();

    // Garante que os dados do localStorage sejam carregados corretamente
    this.flights = this.flightService.getAll()
      .map(flight => ({
        ...flight,
        date: new Date(flight.date) // Converte strings de data para objetos Date
      }))
      .filter(flight => {
        const now = new Date();
        const next48Hours = new Date();
        next48Hours.setHours(now.getHours() + 48);
        return flight.status === FlightStatus.CONFIRMED && flight.date >= now && flight.date <= next48Hours;
      })
      .sort((a, b) => a.date.getTime() - b.date.getTime());
  }

  cancelFlight(flight: Flight) {
    if (flight.status === FlightStatus.CONFIRMED) {
      const confirmation = confirm(`Tem certeza que deseja cancelar o voo ${flight.originAirport} -> ${flight.destinationAirport}?`);
      if (!confirmation) {
        return;
      }

      flight.status = FlightStatus.CANCELED;
      this.flightService.update(flight);

      // Cancela todas as reservas associadas ao voo
      const bookings = this.bookingService.getAll().filter(booking => booking.flight.ID === flight.ID);
      bookings.forEach(booking => {
        booking.status = BookingStatus.FLIGHT_CANCELED;
        this.bookingService.update(booking);
      });

      this.flights = this.flights.filter(f => f.ID !== flight.ID); // Remove da lista exibida
      this.successMessage = `Voo ${flight.originAirport} -> ${flight.destinationAirport} cancelado com sucesso!`;
      console.log(`Voo cancelado: ${flight.originAirport} -> ${flight.destinationAirport}`);
    }
  }

  markAsCompleted(flight: Flight) {
    if (flight.status === FlightStatus.CONFIRMED) {
      const confirmation = confirm(`Tem certeza que deseja marcar o voo ${flight.originAirport} -> ${flight.destinationAirport} como realizado?`);
      if (!confirmation) {
        return;
      }

      flight.status = FlightStatus.REALIZED;
      this.flightService.update(flight);

      // Atualiza todas as reservas associadas ao voo
      const bookings = this.bookingService.getAll().filter(booking => booking.flight.ID === flight.ID);
      bookings.forEach(booking => {
        if (booking.status === BookingStatus.SHIPPED) {
          booking.status = BookingStatus.REALIZED; // Reserva realizada
        } else {
          booking.status = BookingStatus.NOT_REALIZED; // Reserva não realizada
        }
        this.bookingService.update(booking);
      });

      this.flights = this.flights.filter(f => f.ID !== flight.ID); // Remove da lista exibida
      this.successMessage = `Voo ${flight.originAirport} -> ${flight.destinationAirport} marcado como realizado com sucesso!`;
      console.log(`Voo realizado: ${flight.originAirport} -> ${flight.destinationAirport}`);
    }
  }
}
