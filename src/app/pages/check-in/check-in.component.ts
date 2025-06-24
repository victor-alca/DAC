import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../services/client/client.service';
import { BookingService } from '../../services/booking/booking.service';
import { AuthService } from '../../services/auth/auth.service';
import { ClientDTO } from '../../shared/dtos/clientDto';

interface ReservaCheckIn {
  codigo: string;
  data: string;
  valor: number;
  milhas_utilizadas: number;
  quantidade_poltronas: number;
  codigo_cliente: number;
  estado: string;
  voo: {
    codigo: string;
    data: string;
    valor_passagem: number;
    quantidade_poltronas_total: number;
    quantidade_poltronas_ocupadas: number;
    estado: string;
    aeroporto_origem: {
      codigo: string;
      nome: string;
      cidade: string;
      uf: string;
    };
    aeroporto_destino: {
      codigo: string;
      nome: string;
      cidade: string;
      uf: string;
    };
  };
}

@Component({
  selector: 'app-check-in',
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.css',
})
export class CheckInComponent implements OnInit {
  reservas: ReservaCheckIn[] = [];
  reservasCheckIn: ReservaCheckIn[] = [];
  loading: boolean = true;
  error: string | null = null;
  processingReserva: string | null = null;

  constructor(
    private clientService: ClientService,
    private bookingService: BookingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserBookings();
  }

  loadUserBookings(): void {
    const user = this.authService.getCurrentUserData() as ClientDTO;
    
    if (!user || !user.codigo) {
      this.error = 'Usuário não encontrado. Faça login novamente.';
      this.loading = false;
      return;
    }

    this.clientService.getClientBookings(user.codigo).subscribe({
      next: (reservas: any) => {
        if (reservas && reservas.length > 0) {
          this.reservas = reservas;
          this.filterCheckInEligibleBookings();
        } else {
          this.reservas = [];
          this.reservasCheckIn = [];
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar reservas:', error);
        if (error.status === 204) {
          // Sem conteúdo - cliente não tem reservas
          this.reservas = [];
          this.reservasCheckIn = [];
        } else {
          this.error = 'Erro ao carregar suas reservas.';
        }
        this.loading = false;
      }
    });
  }

  filterCheckInEligibleBookings(): void {
    const now = new Date();
    
    this.reservasCheckIn = this.reservas.filter(reserva => {
      // Verifica se a reserva está no estado CRIADA
      if (reserva.estado !== 'CRIADA') {
        return false;
      }

      // Verifica se o voo está CONFIRMADO
      if (reserva.voo.estado !== 'CONFIRMADO') {
        return false;
      }

      const flightDate = new Date(reserva.voo.data);
      const diffInHours = (flightDate.getTime() - now.getTime()) / (1000 * 60 * 60);
      
      // Voo deve estar nas próximas 48 horas e ainda não ter acontecido
      return diffInHours <= 48 && diffInHours > 0;
    });
  }

  canCheckIn(reserva: ReservaCheckIn): boolean {
    if (!reserva) return false;
    
    const now = new Date();
    const flightDate = new Date(reserva.voo.data);
    const diffInHours = (flightDate.getTime() - now.getTime()) / (1000 * 60 * 60);
    
    // Pode fazer check-in se:
    // - O voo está nas próximas 48 horas
    // - O voo ainda não aconteceu (diffInHours > 0)
    // - A reserva está no estado CRIADA
    // - O voo está CONFIRMADO
    return diffInHours <= 48 && 
           diffInHours > 0 && 
           reserva.estado === 'CRIADA' && 
           reserva.voo.estado === 'CONFIRMADO';
  }

  checkIn(reserva: ReservaCheckIn): void {
    if (!this.canCheckIn(reserva)) {
      alert('Não é possível fazer check-in para esta reserva no momento.');
      return;
    }

    if (!confirm(`Deseja realizar o check-in para o voo ${reserva.voo.aeroporto_origem.cidade} → ${reserva.voo.aeroporto_destino.cidade}?`)) {
      return;
    }

    this.processingReserva = reserva.codigo;
    this.error = null;

    this.bookingService.updateBookingStatus(reserva.codigo, 'CHECK-IN').subscribe({
      next: (response: any) => {
        if (response) {
          // Atualiza o estado da reserva na lista
          const index = this.reservas.findIndex(r => r.codigo === reserva.codigo);
          if (index !== -1) {
            this.reservas[index].estado = 'CHECK-IN';
          }
          
          // Remove da lista de check-in disponível
          this.filterCheckInEligibleBookings();
          
          alert(`Check-in realizado com sucesso para o voo ${reserva.voo.aeroporto_origem.cidade} → ${reserva.voo.aeroporto_destino.cidade}!`);
        }
        this.processingReserva = null;
      },
      error: (error) => {
        console.error('Erro ao fazer check-in:', error);
        let errorMessage = 'Erro ao fazer check-in. Tente novamente.';
        
        if (error.status === 403) {
          errorMessage = 'Você não tem permissão para fazer check-in nesta reserva.';
        } else if (error.status === 400) {
          errorMessage = 'Não é possível fazer check-in nesta reserva no momento.';
        }
        
        this.error = errorMessage;
        this.processingReserva = null;
      }
    });
  }

  getTimeUntilFlight(flightDate: string): string {
    const now = new Date();
    const flight = new Date(flightDate);
    const diffInHours = Math.floor((flight.getTime() - now.getTime()) / (1000 * 60 * 60));
    
    if (diffInHours <= 0) {
      return 'Voo já aconteceu';
    } else if (diffInHours < 24) {
      return `${diffInHours}h`;
    } else {
      const days = Math.floor(diffInHours / 24);
      const hours = diffInHours % 24;
      return `${days}d ${hours}h`;
    }
  }
}