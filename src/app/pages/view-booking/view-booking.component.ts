import { Component } from '@angular/core';

@Component({
  selector: 'app-view-booking',
  templateUrl: './view-booking.component.html',
  styleUrl: './view-booking.component.css'
})
export class ViewBookingComponent {
  reservas = [
    { id: 1, data: '2024-03-22', origem: 'GRU', destino: 'JFK' },
    { id: 2, data: '2024-04-15', origem: 'GIG', destino: 'LIS' }
  ];
}
