export class CreateFlightDTO {
    constructor(
        public data: string,
        public valor_passagem: number,
        public quantidade_poltronas_total: number,
        public quantidade_poltronas_ocupadas: number = 0,
        public codigo_aeroporto_origem: string,
        public codigo_aeroporto_destino: string,
        public codigo: string = ""
    ) {}
}


