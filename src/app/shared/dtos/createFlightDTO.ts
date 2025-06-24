import { AirportDTO } from "./airportDTO";

export class CreateFlightDTO {
    constructor(
        public codigo: string = "",
        public data: string,
        public valor_passagem: number,
        public quantidade_poltronas_total: number,
        public quantidade_poltronas_ocupadas: number = 0,
        public estado: string = "",
        public aeroporto_origem: AirportDTO,
        public aeroporto_destino: AirportDTO,

    ) {}
}


