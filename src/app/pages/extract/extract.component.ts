import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../services/client/client.service';
import { AuthService } from '../../services/auth/auth.service';
import { ClientDTO } from '../../shared/dtos/clientDto';
import { ExtractResponseDTO } from '../../shared/dtos/transacaoDTO';

interface Transacao {
  data: Date;
  codigoReserva?: string;
  valorReais: number;
  milhas: number;
  descricao: string;
  tipo: 'ENTRADA' | 'SAIDA';
}

@Component({
  selector: 'app-extract',
  templateUrl: './extract.component.html',
  styleUrl: './extract.component.css'
})
export class ExtractComponent implements OnInit {
  transacoes: Transacao[] = [];
  saldoMilhas: number = 0;
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private clientService: ClientService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadExtractData();
  }

  loadExtractData(): void {
    const user = this.authService.getCurrentUserData() as ClientDTO;
    
    if (!user || !user.codigo) {
      this.error = 'Usuário não encontrado. Faça login novamente.';
      this.loading = false;
      return;
    }

    this.clientService.getMilesTransactions({ code: user.codigo } as any).subscribe({
      next: (response: ExtractResponseDTO) => {
        if (response && response.transacoes) {
          this.saldoMilhas = response.saldo_milhas;
          this.transacoes = response.transacoes.map(transacao => ({
            data: new Date(transacao.data),
            codigoReserva: transacao.codigo_reserva || undefined,
            valorReais: transacao.valor_reais,
            milhas: Math.abs(transacao.quantidade_milhas), 
            descricao: transacao.descricao,
            tipo: transacao.tipo
          }));
        } else {
          this.transacoes = [];
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar extrato:', error);
        this.error = 'Erro ao carregar o extrato de transações.';
        this.loading = false;
      }
    });
  }
}