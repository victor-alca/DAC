import { Airport } from "../models/airport/airport.model";

export class CreateFlightResponseDTO {
    constructor(
        public codigo: string,
        public data: Date,
        public valor_passagem: number,
        public quantidade_poltronas: number,
        public quantidade_poltronas_utilizadas: number,
        public estado: string,
        public aeroporto_origem: Airport,
        public aeroporto_destino: Airport
    ) {}
}
