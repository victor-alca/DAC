export class CreateBookingDTO {
    constructor(
        public codigo_cliente: number,
        public valor: number,
        public milhas_utilizadas: number,
        public quantidade_poltronas: number,
        public codigo_voo: string,
        public codigo_aeroporto_origem: string,
        public codigo_aeroporto_destino: string,
    ) {}
}


