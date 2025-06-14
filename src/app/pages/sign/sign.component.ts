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
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GenericModalComponent } from '../generic-modal/generic-modal.component';


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

  constructor(private modalService: NgbModal, private router: Router, private ClientService: ClientService) {}

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
          this.openModal("Cadastro realizado com sucesso!", "Sua senha de acesso foi enviada ao email cadastrado.")
          this.router.navigate(['/login']);
        },
        error: (err) => {
          if(err.status == 409){
            this.openModal("Ocorreu um erro.", "O cliente já existe.")
          }else{
            this.openModal("Ocorreu um erro.", "Ocorreu um erro ao cadastrar o cliente, tente novamente.")
          }
          console.error(err);
        }
      });
    }
  }

  openModal(title: string, text: string){
    const modalRef = this.modalService.open(GenericModalComponent)
    modalRef.componentInstance.title = title
    modalRef.componentInstance.text = text
    modalRef.result.then(() => {
    this.router.navigate(['/login'])});
  }
}


