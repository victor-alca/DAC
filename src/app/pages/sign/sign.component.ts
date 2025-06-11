import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Person } from '../../shared/models/person.model';
import { CommonModule } from '@angular/common';
import { CpfValidatorDirective } from '../../shared/directives/cpf-validator.directive';
import { NgxMaskDirective } from 'ngx-mask';
import { EmailValidatorDirective } from '../../shared/directives/email-validator.directive';
import { PhoneValidatorDirective } from '../../shared/directives/phone-validator.directive';
import { CepValidatorDirective } from '../../shared/directives/cep-validator.directive';
import { Router } from '@angular/router';
import { ClientDTO } from '../../shared/models/sing/clientDto';
import { ClientService } from '../../services/client/client.service';


@Component({
  selector: 'app-sign',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CpfValidatorDirective,
    EmailValidatorDirective,
    NgxMaskDirective,
    CepValidatorDirective,
  ],
  templateUrl: './sign.component.html',
  styleUrl: './sign.component.css',
})
export class SignComponent {
  @ViewChild('formPerson') formPerson!: NgForm;
  clientDTO: ClientDTO = new ClientDTO();

  cep: string = '';
  address: string = '';
  neighborhood: string = '';
  city: string = '';
  state: string = '';
  cepError: string | null = null;
  number: string = '';

  constructor(private router: Router, private ClientService: ClientService) {}

  submit(): void {
    if (this.formPerson.form.valid) { 
      this.clientDTO.endereco.cep = this.cep;
      this.clientDTO.endereco.rua = this.address;
      this.clientDTO.endereco.bairro = this.neighborhood;
      this.clientDTO.endereco.cidade = this.city;
      this.clientDTO.endereco.uf = this.state;
      this.clientDTO.endereco.numero = this.number;
      this.ClientService.create(this.clientDTO).subscribe({
        next: () => {
          alert('Cadastro realizado com sucesso! Sua senha foi enviada para o seu email.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          if(err.status == 409){
            alert(`Erro ao cadastrar cliente! O cliente ja existe.`);
          }else{
            alert("Ocorreu um erro ao cadastrar o cliente.")
          }
          console.error(err);
        }
      });
    }
  }
}
