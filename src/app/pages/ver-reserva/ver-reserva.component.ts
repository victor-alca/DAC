import { Component } from '@angular/core';

@Component({
  selector: 'app-ver-reserva',
  templateUrl: './ver-reserva.component.html',
  styleUrl: './ver-reserva.component.css'
})
export class VerReservaComponent {
  reservas = [
    { id: 1, data: '2024-03-22', origem: 'GRU', destino: 'JFK' },
    { id: 2, data: '2024-04-15', origem: 'GIG', destino: 'LIS' }
  ];
}
