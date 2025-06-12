export class Endereco {
    constructor(
        public cep: string = '',
        public uf: string = '',
        public cidade: string = '',
        public bairro: string = '',
        public rua: string = '',
        public numero: string = '',
        public complemento: string = ''
    ) {}
}

export class ClientDTO {
    constructor(
        public cpf: string = '',
        public email: string = '',
        public nome: string = '',
        public saldo_milhas: number = 0,
        public endereco: Endereco = new Endereco()
    ) {}
}

