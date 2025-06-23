export class EmployeeDTO {
    constructor(
        public codigo: number = 0,
        public cpf: string = '',
        public email: string = '',
        public nome: string | null = null,
        public telefone: string | null = null,
        public tipo: string = ''
    ) {}
}