import { Component } from '@angular/core';

@Component({
  selector: 'app-customer-home-cliente',
  templateUrl: './customer-home.component.html',
  styleUrl: './customer-home.component.css'
})
export class CustomerHomeComponent {
  reservas = [
    { id: 1, data: '2024-03-22', origem: 'GRU', destino: 'JFK' },
    { id: 2, data: '2024-04-15', origem: 'GIG', destino: 'LIS' }
  ];
}
