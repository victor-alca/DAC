import { Injectable } from '@angular/core';
import { Flight } from '../shared/models/flight/flight.model';

// const para o local storage
const LS_KEY = 'flights';

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  constructor() {}

  getAll(): Flight[] {
    const flights = localStorage[LS_KEY];
    return flights ? JSON.parse(flights) : [];
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
}
