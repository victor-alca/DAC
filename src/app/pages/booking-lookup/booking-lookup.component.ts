import { Component } from '@angular/core';
import { BookingService } from '../../services/booking/booking.service';

interface BookingResponse {
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
  selector: 'app-booking-lookup',
  templateUrl: './booking-lookup.component.html',
  styleUrl: './booking-lookup.component.css'
})
export class BookingLookupComponent {
  bookingId: string = '';
  reserva: BookingResponse | null = null;
  errorMessage: string | null = null;
  loading: boolean = false;
  processingAction: boolean = false;

  constructor(private bookingService: BookingService) {}

  onSubmit(): void {
    this.errorMessage = null;
    this.reserva = null;
    this.loading = true;

    if (!this.bookingId.trim()) {
      this.errorMessage = 'Por favor, insira um código de reserva válido.';
      this.loading = false;
      return;
    }

    this.bookingService.getById(this.bookingId.trim()).subscribe({
      next: (response: any) => {
        if (response) {
          this.reserva = response;
        } else {
          this.errorMessage = 'Reserva não encontrada.';
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao buscar reserva:', error);
        if (error.status === 404) {
          this.errorMessage = 'Reserva não encontrada.';
        } else {
          this.errorMessage = 'Erro ao buscar reserva. Tente novamente.';
        }
        this.loading = false;
      }
    });
  }

  canCheckIn(): boolean {
    if (!this.reserva) return false;
    
    const now = new Date();
    const flightDate = new Date(this.reserva.voo.data);
    const diffInHours = (flightDate.getTime() - now.getTime()) / (1000 * 60 * 60);
    
    // Pode fazer check-in se:
    // - O voo está nas próximas 48 horas
    // - O voo ainda não aconteceu (diffInHours > 0)
    // - A reserva está no estado CRIADA
    // - O voo está CONFIRMADO
    return diffInHours <= 48 && 
           diffInHours > 0 && 
           this.reserva.estado === 'CRIADA' && 
           this.reserva.voo.estado === 'CONFIRMADO';
  }

  onCheckIn(): void {
    if (!this.reserva) return;
    
    this.processingAction = true;
    this.errorMessage = null;

    this.bookingService.updateBookingStatus(this.reserva.codigo, 'CHECK-IN').subscribe({
      next: (response: any) => {
        if (response) {
          // Atualiza os dados da reserva com a resposta
          this.reserva!.estado = response.estado;
          alert('Check-In realizado com sucesso!');
        }
        this.processingAction = false;
      },
      error: (error) => {
        console.error('Erro ao fazer check-in:', error);
        if (error.status === 403) {
          this.errorMessage = 'Você não tem permissão para fazer check-in nesta reserva.';
        } else if (error.status === 400) {
          this.errorMessage = 'Não é possível fazer check-in nesta reserva no momento.';
        } else {
          this.errorMessage = 'Erro ao fazer check-in. Tente novamente.';
        }
        this.processingAction = false;
      }
    });
  }

   onCancel(): void {
    if (!this.reserva) return;

    const confirmCancel = confirm('Tem certeza de que deseja cancelar esta reserva?');
    if (!confirmCancel) return;

    this.processingAction = true;
    this.errorMessage = null;

    this.bookingService.delete(this.reserva.codigo).subscribe({
      next: (response: any) => {
        if (response) {
          // Atualiza os dados da reserva com a resposta
          this.reserva!.estado = response.estado || 'CANCELADA';
          alert('Reserva cancelada com sucesso!');
        }
        this.processingAction = false;
      },
      error: (error) => {
        console.error('Erro ao cancelar reserva:', error);
        if (error.status === 403) {
          this.errorMessage = 'Você não tem permissão para cancelar esta reserva.';
        } else if (error.status === 400) {
          this.errorMessage = 'Não é possível cancelar esta reserva no momento.';
        } else if (error.status === 404) {
          this.errorMessage = 'Reserva não encontrada.';
        } else {
          this.errorMessage = 'Erro ao cancelar reserva. Tente novamente.';
        }
        this.processingAction = false;
      }
    });
  }

  getBookingStatusText(status: string): string {
    switch (status) {
      case 'CRIADA':
        return 'Criada';
      case 'CHECK-IN':
        return 'Check-In Realizado';
      case 'CANCELADA':
        return 'Cancelada';
      case 'EMBARCADA':
        return 'Embarcado';
      case 'REALIZADA':
        return 'Realizada';
      case 'NÃO REALIZADA':
        return 'Não Realizada';
      default:
        return status;
    }
  }

  getFlightStatusText(status: string): string {
    switch (status) {
      case 'CONFIRMADO':
        return 'Confirmado';
      case 'CANCELADO':
        return 'Cancelado';
      case 'REALIZADO':
        return 'Realizado';
      default:
        return status;
    }
  }
}