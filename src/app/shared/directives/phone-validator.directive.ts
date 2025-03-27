import { Directive } from '@angular/core';
import { FormControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[phone]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: PhoneValidatorDirective,
      multi: true,
    },
  ],
})
export class PhoneValidatorDirective {
  constructor() {}

  validate(field: FormControl): ValidationErrors | null {
    const phone = field.value;
    if (!phone) return null;

    const isValidPhone = String(phone).replace(/\D/g, '').length >= 10;

    if (isValidPhone) return null;
    return {
      phone: true,
    };
  }
}
