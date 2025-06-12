export class Client {

    constructor (
        public code: number = 0,

        public cpf: string = "",
        public name: string = "",
        public miles: number = 0,
        public email: string = "",
        public phone: string = "",
        public password: string = "",
        
        public cep: string = "",
        public federativeUnit: string = "",
        public city: string = "",
        public neighborhood: string = "",
        public street: string = "",
        public number?: string,
        public complement?: string,
    ){}
}

