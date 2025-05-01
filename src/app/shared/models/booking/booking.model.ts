import { Flight } from '../flight/flight.model';
import { BookingStatus } from './booking-status.enum';

export class Booking {
  constructor(
    public ID: number,
    public flight: Flight,
    public date: Date,
    public status: BookingStatus,
    public moneySpent: number,
    public milesSpent: number,
  ) {}
}
