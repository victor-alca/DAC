export class Client {

    constructor (
        public ID: number = 0,

        
        public CPF: string = "",
        public name: string = "",
        public miles: number = 0,
        public email: string = "",
        public phone: string = "",
        public password: string = "",
        
        public CEP: string = "",
        public state: string = "",
        public city: string = "",
        public neighborhood: string = "",
        public street: string = "",
        public number?: string,
        public complement?: string,
    ){}
}
