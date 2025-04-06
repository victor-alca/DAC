import { FlightStatus } from './flight-status.enum';

export class Flight {
  constructor(
    public ID: string,
    public date: Date,
    public originAirport: string,
    public destinationAirport: string,
    public ticketCost: number,
    public totalSeats: number,
    public occupatedSeats: number,
    public status: FlightStatus
  ) {}
}
