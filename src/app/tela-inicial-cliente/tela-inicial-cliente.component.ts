import { Component } from '@angular/core';

@Component({
  selector: 'app-tela-inicial-cliente',
  templateUrl: './tela-inicial-cliente.component.html',
  styleUrl: './tela-inicial-cliente.component.css'
})
export class TelaInicialClienteComponent {
  reservas = [
    { id: 1, data: '2024-03-22', origem: 'GRU', destino: 'JFK' },
    { id: 2, data: '2024-04-15', origem: 'GIG', destino: 'LIS' }
  ];
}
