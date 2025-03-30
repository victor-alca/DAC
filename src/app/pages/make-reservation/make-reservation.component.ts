import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-make-reservation',
  templateUrl: './make-reservation.component.html',
  styleUrl: './make-reservation.component.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class MakeReservationComponent {

  ngOnInit(){
    this.filterTable();
  }

  reservations = [
    { id: 1, data: new Date(2025, 4, 1), origin: 'GRU', destination: 'JFK' },  
    { id: 2, data: new Date(2025, 3, 25), origin: 'CWB', destination: 'LIS' },  
    { id: 3, data: new Date(2025, 5, 5), origin: 'GRU', destination: 'MIA' },  
    { id: 4, data: new Date(2025, 3, 15), origin: 'BSB', destination: 'MAD' },  
    { id: 5, data: new Date(2025, 4, 20), origin: 'BSB', destination: 'CDG' },  
    { id: 6, data: new Date(2025, 2, 31), origin: 'VCP', destination: 'FCO' },  
    { id: 7, data: new Date(2025, 3, 10), origin: 'CWB', destination: 'EZE' },  
    { id: 8, data: new Date(2025, 3, 5), origin: 'REC', destination: 'LHR' },  
    { id: 9, data: new Date(2025, 4, 10), origin: 'REC', destination: 'MEX' },  
    { id: 10, data: new Date(2025, 3, 20), origin: 'CWB', destination: 'SCL' }  
];


  filteredReservations = [...this.reservations];

  searchOrigin: string = '';
  searchDestination: string = '';

  filterTable() {
    this.filteredReservations = this.reservations
        .filter(r => 
            r.origin.toLowerCase().includes(this.searchOrigin.toLowerCase()) &&
            r.destination.toLowerCase().includes(this.searchDestination.toLowerCase())
        )
        .sort((a, b) => new Date(a.data).getTime() - new Date(b.data).getTime());
}

}
