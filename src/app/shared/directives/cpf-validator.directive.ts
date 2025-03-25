import { Directive } from '@angular/core';
import { FormControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[cpf]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: CpfValidatorDirective,
      multi: true,
    },
  ],
})
export class CpfValidatorDirective {
  constructor() {}

  validate(field: FormControl): ValidationErrors | null {
    if (!field.value) return null
    const cpf = field.value?.replace(/\D/g, '');
    let soma;
    let resto;
    soma = 0;
    if (cpf == '00000000000')
      return {
        cpf: true,
      };

    for (let i = 1; i <= 9; i++)
      soma = soma + parseInt(cpf.substring(i - 1, i)) * (11 - i);
    resto = (soma * 10) % 11;

    if (resto == 10 || resto == 11) resto = 0;
    if (resto != parseInt(cpf.substring(9, 10)))
      return {
        cpf: true,
      };

    soma = 0;
    for (let i = 1; i <= 10; i++)
      soma = soma + parseInt(cpf.substring(i - 1, i)) * (12 - i);
    resto = (soma * 10) % 11;

    if (resto == 10 || resto == 11) resto = 0;
    if (resto != parseInt(cpf.substring(10, 11)))
      return {
        cpf: true,
      };
    return null;
  }
}
