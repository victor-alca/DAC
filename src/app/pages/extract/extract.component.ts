import { Component } from '@angular/core';

interface Transacao {
  data: Date;
  codigoReserva?: number;
  valorReais: number;
  milhas: number;
  descricao: string;
  tipo: 'ENTRADA' | 'SAÍDA';
}

@Component({
  selector: 'app-extract',
  templateUrl: './extract.component.html',
  styleUrl: './extract.component.css'
})
export class ExtractComponent {
  transacoes: Transacao[] = [
    { data: new Date('2024-03-22'), codigoReserva: 101, valorReais: 0, milhas: 5000, descricao: 'CWB->GRU', tipo: 'SAÍDA' },
    { data: new Date('2024-04-10'), codigoReserva: 102, valorReais: 0, milhas: 7000, descricao: 'GRU->MIA', tipo: 'SAÍDA' },
    { data: new Date('2024-05-05'), valorReais: 300, milhas: 3000, descricao: 'COMPRA DE MILHAS', tipo: 'ENTRADA' }
  ];
}
