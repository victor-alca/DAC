import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-employee-dashboard',
  templateUrl: './employee-dashboard.component.html',
  styleUrls: ['./employee-dashboard.component.css']
})
export class EmployeeDashboardComponent implements OnInit {
  flights: any[] = [];

  ngOnInit() {
    // Mock de voos (simulação de dados, depois virá do service)
    const allFlights = [
      { dateTime: '2025-03-22T10:00:00', origin: 'CWB', destination: 'GRU' },
      { dateTime: '2025-03-23T14:30:00', origin: 'GRU', destination: 'GIG' },
      { dateTime: '2025-03-21T08:00:00', origin: 'BSB', destination: 'POA' }, 
      { dateTime: '2025-03-24T20:15:00', origin: 'REC', destination: 'FOR' },
    ];

    const now = new Date();
    const next48Hours = new Date();
    next48Hours.setHours(now.getHours() + 48);

    // Filtra voos dentro das próximas 48h
    this.flights = allFlights
      .map(flight => ({
        ...flight,
        dateTime: new Date(flight.dateTime) // Converte string para Date
      }))
      .filter(flight => flight.dateTime >= now && flight.dateTime <= next48Hours)
      .sort((a, b) => a.dateTime.getTime() - b.dateTime.getTime()); // Ordena por data crescente
  }

  cancelFlight(flight: any) {
    console.log(`Cancelando voo: ${flight.origin} -> ${flight.destination}`);
  }

  markAsCompleted(flight: any) {
    console.log(`Marcando voo como concluído: ${flight.origin} -> ${flight.destination}`);
  }
}
