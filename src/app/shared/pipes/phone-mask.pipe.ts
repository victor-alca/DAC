import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phoneMask'
})
export class PhoneMaskPipe implements PipeTransform {
  transform(value: string | number): string {
    if (!value) return '';

    const phone = value.toString().replace(/\D/g, '');

    if (phone.length === 10) {
      // Format: (XX) XXXX-XXXX
      return phone.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    } else if (phone.length === 11) {
      // Format: (XX) XXXXX-XXXX
      return phone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    }

    return value.toString(); // Return as is if format is not recognized
  }
}