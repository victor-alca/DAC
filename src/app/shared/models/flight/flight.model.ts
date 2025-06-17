import { Airport } from '../airport/airport.model';
import { FlightStatus } from './flight-status.enum';

export class Flight {
  constructor(
    public codigo: string,
    public data: Date,
    public aeroporto_origem: Airport,
    public aeroporto_destino: Airport,
    public valor_passagem: number,
    public quantidade_poltronas_total: number,
    public quantidade_poltronas_ocupadas: number,
    public estado: FlightStatus
  ) {}
}
