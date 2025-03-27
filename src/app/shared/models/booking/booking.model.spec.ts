import { Booking } from './booking.model';

describe('Booking', () => {
  it('should create an instance', () => {
    expect(new Booking(1, 101, new Date('2023-01-01'), 1)).toBeTruthy();
  });
});
