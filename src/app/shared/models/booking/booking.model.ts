import { BookingStatus } from './booking-status.enum';

export class Booking {
  constructor(
    public ID: number,
    public flightId: number,
    public date: Date,
    public status: BookingStatus,
    public code: string[6]
  ) {}
}
