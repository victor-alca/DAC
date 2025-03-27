import { FlightStatus } from './flight-status.enum';

export class Flight {
  constructor(
    public ID: number,
    public date: Date,
    public originAirport: string,
    public destinationAirport: string,
    public ticketCost: number,
    public totalSeats: string,
    public occupatedSeats: string,
    public status: FlightStatus
  ) {}
}
