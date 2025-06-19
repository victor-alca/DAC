export class TransacaoDTO {
  constructor(
    public data: string = '',
    public valor_reais: number = 0,
    public quantidade_milhas: number = 0,
    public descricao: string = '',
    public codigo_reserva: string = '',
    public tipo: 'ENTRADA' | 'SAIDA' = 'ENTRADA',
    public quantidadeMilhas?: number
  ) {}
}

export class ExtractResponseDTO {
  constructor(
    public codigo: number = 0,
    public saldo_milhas: number = 0,
    public transacoes: TransacaoDTO[] = []
  ) {}
}