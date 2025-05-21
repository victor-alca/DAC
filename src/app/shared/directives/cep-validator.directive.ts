import { Directive, HostListener, Input, Output, EventEmitter } from '@angular/core';
import { NgModel } from '@angular/forms';

@Directive({
  selector: '[cep]',
  standalone: true,
})
export class CepValidatorDirective {
  @Input() cep!: string;
  @Input() address!: string;
  @Input() neighborhood!: string;
  @Input() city!: string;
  @Input() state!: string;
  @Output() addressChange = new EventEmitter<string>();
  @Output() neighborhoodChange = new EventEmitter<string>();
  @Output() cityChange = new EventEmitter<string>();
  @Output() stateChange = new EventEmitter<string>();
  @Output() cepErrorChange = new EventEmitter<string | null>();

  constructor(private ngModel: NgModel) {}

  @HostListener('blur') async onCepBlur() {
    const cep = this.ngModel.value;
    let cepError: string | null = null;

    const onlyNumbers = /^[0-9]+$/;
    const cepValid = /^[0-9]{8}$/;

    if (!onlyNumbers.test(cep) || !cepValid.test(cep)) {
      cepError = 'CEP inválido';
      this.ngModel.control.setErrors({ cepInvalid: true });
      this.cepErrorChange.emit(cepError);
      this.updateNgModel();
      return;
    }

    try {
      const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
      if (!response.ok) {
        throw new Error('Erro ao buscar o CEP');
      }

      const responseCep = await response.json();
      this.address = responseCep.logradouro || '';
      this.neighborhood = responseCep.bairro || '';
      this.city = responseCep.localidade || '';
      this.state = responseCep.uf || '';

      this.addressChange.emit(this.address);
      this.neighborhoodChange.emit(this.neighborhood);
      this.cityChange.emit(this.city);
      this.stateChange.emit(this.state);

      cepError = null;
      this.ngModel.control.setErrors(null); // Limpa erros customizados
    } catch (error) {
      cepError = 'Erro ao buscar o CEP. Tente novamente.';
      this.ngModel.control.setErrors({ cepInvalid: true });
    }

    this.cepErrorChange.emit(cepError);
    this.updateNgModel();
  }

  private updateNgModel() {
    this.ngModel.control.setValue(this.ngModel.value, { emitEvent: false });
    this.ngModel.control.markAsTouched();
    this.ngModel.control.updateValueAndValidity();
  }
}