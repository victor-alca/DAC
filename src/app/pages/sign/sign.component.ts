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
import { Client } from '../../shared/models/client/client';
import { ClientService } from '../../services/client/client.service';


@Component({
  selector: 'app-sign',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CpfValidatorDirective,
    EmailValidatorDirective,
    PhoneValidatorDirective,
    NgxMaskDirective,
    CepValidatorDirective,
  ],
  templateUrl: './sign.component.html',
  styleUrl: './sign.component.css',
})
export class SignComponent {
  @ViewChild('formPerson') formPerson!: NgForm;
  client: Client = new Client();

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
      this.client.CEP = this.cep;
      this.client.street = this.address;
      this.client.neighborhood = this.neighborhood;
      this.client.city = this.city;
      this.client.state = this.state;
      this.client.number = this.number;

      this.ClientService.create(this.client)
    }
  }
}
