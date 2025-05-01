export class Login {
  constructor(
    public email: string = "",
    public password: string = "",
    public role: 'user' | 'employee' = 'user' // Adicionado para diferenciar o tipo de usuário temporariamente
  ) {

  }
}
