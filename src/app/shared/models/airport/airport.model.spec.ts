import { Airport } from './airport.model';

describe('Airport', () => {
  it('should create an instance', () => {
    expect(new Airport('ABC', 'Test Airport', 'Test City', 'TU')).toBeTruthy();
  });
});
