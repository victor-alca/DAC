export class Client {

    constructor (
        private ID: string,

        public status: boolean,

        public CPF: string,
        public name: string,
        public miles: number,

        public CEP: string,
        public street: string,
        public neighborhood: string,
        public city: string,
        public number?: string,
        public complement?: string,
    ){}
}
