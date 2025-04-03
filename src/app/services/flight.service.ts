import { Injectable } from '@angular/core';
import { Flight } from '../shared/models/flight/flight.model';
import { FlightStatus } from '../shared/models/flight/flight-status.enum';

// const para o local storage
const LS_KEY = 'flights';

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  constructor() {}

  getAll(): Flight[] {
    const flights = localStorage[LS_KEY];
    return flights ? JSON.parse(flights).map((flight: any) => ({
      ...flight,
      date: new Date(flight.date) // Converte strings de data para objetos Date
    })) : [];
  }

  create(flight: Flight): void {
    const flights = this.getAll();

    flight.ID = new Date().getTime();
    flights.push(flight);

    localStorage[LS_KEY] = JSON.stringify(flights);
  }

  getById(id: number): Flight | undefined {
    const flights = this.getAll();
    return flights.find((flight) => flight.ID === id);
  }

  update(flight: Flight): void {
    const flights = this.getAll();

    flights.forEach((obj, index, objs) => {
      if (flight.ID === obj.ID) {
        objs[index] = flight;
      }
    });

    localStorage[LS_KEY] = JSON.stringify(flights);
  }

  delete(id: number): void {
    let flights = this.getAll();

    flights = flights.filter((flight) => flight.ID !== id);
    localStorage[LS_KEY] = JSON.stringify(flights);
  }

  // Método temporário para inserir voos manualmente
  seedFlights(): void {
    const flights: Flight[] = [
      new Flight(1, new Date('2025-04-03T12:10:03.087Z'), 'CWB', 'GRU', 500, '180', '50', FlightStatus.CONFIRMED),
      new Flight(2, new Date('2025-04-04T00:10:03.087Z'), 'GRU', 'GIG', 400, '200', '120', FlightStatus.CONFIRMED),
      new Flight(3, new Date('2025-04-04T11:10:03.087Z'), 'BSB', 'POA', 300, '150', '80', FlightStatus.CANCELED),
      new Flight(4, new Date('2025-04-04T12:10:03.087Z'), 'REC', 'FOR', 600, '100', '60', FlightStatus.REALIZED),
    ];
    localStorage[LS_KEY] = JSON.stringify(flights);
  }
}
