import { Flight } from "../models/flight/flight.model";

export class CreateBookingResponseDTO {
    constructor(
        public codigo: string,
        public codigo_cliente: number,
        public data: Date,
        public estado: string,
        public milhas_utilizadas: string,
        public quantidade_poltronas: number,
        public valor: number,
        public voo: Flight
    ) {}
}
