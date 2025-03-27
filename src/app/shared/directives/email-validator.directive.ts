import { Directive } from '@angular/core';
import { FormControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[email]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: EmailValidatorDirective,
      multi: true,
    },
  ],
})
export class EmailValidatorDirective {
  constructor() {}

  validate(field: FormControl): ValidationErrors | null {
    const email = field.value;
    if (!email) return null

    const isValidEmail = String(email)
      .toLowerCase()
      .match(/^((?!\.)[\w-_.]*[^.])(@\w+)(\.\w+(\.\w+)?[^.\W])$/);

    if (isValidEmail) return null;
    return {
      email: true,
    };
  }
}
