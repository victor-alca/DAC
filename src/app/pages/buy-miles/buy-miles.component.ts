import { Component } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { ClientService } from '../../services/client/client.service';
import { ClientDTO } from '../../shared/dtos/clientDto';

@Component({
  selector: 'app-buy-miles',
  templateUrl: './buy-miles.component.html',
  styleUrl: './buy-miles.component.css',
})
export class BuyMilesComponent {

  constructor(private authService: AuthService, private clientService: ClientService) {}

  private _miles: number | null = 1000;
  value: number = this.miles ? this.miles * 5 : 0;
  successMessage: string | null = null;

  get miles(): number | null {
    return this._miles;
  }

  set miles(newValue: number | null) {
    this._miles = newValue;
    this.value = newValue ? newValue * 5 : 0; // Atualiza automaticamente o valor
  }

  updateMiles(value: string) {
    const numericValue = Number(value);
    if (isNaN(numericValue)) {
      this.miles = null;
      this.value = 0;
      return;
    }

    this.miles = numericValue;
    this.value = numericValue * 5;
  }

  submit(): void {
    if (confirm(`Deseja confirmar a compra de ${this.miles} milhas por R$${this.value}?`)) {
      const currentClient = this.authService.getCurrentUserData() as ClientDTO
      console.log(currentClient.codigo)
      this.clientService.addClientMiles(currentClient.codigo, this.miles!).subscribe({
        next: (resp) => {
           this.successMessage = `Compra de ${this.miles} milhas realizada com sucesso por R$${this.value}.`;
           console.log("Compra realizada:", { miles: this.miles, value: this.value });
        },
        error: (err) => {
          alert("Ocorreu um erro na compra. Tente novamente.")
        },
      })
      setTimeout(() => {
        this.successMessage = null;
      }, 5000);
    }
  }
}