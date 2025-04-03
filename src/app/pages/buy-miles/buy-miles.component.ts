import { Component } from '@angular/core';

@Component({
  selector: 'app-buy-miles',
  templateUrl: './buy-miles.component.html',
  styleUrl: './buy-miles.component.css',
})
export class BuyMilesComponent {
 private _miles: number | null = 1000;
  value: number = this.miles ? this.miles * 5 : 0;

  get miles(): number | null {
    return this._miles;
  }

  set miles(newValue: number | null) {
    this._miles = newValue;
    this.value = newValue ? newValue * 5 : 0; // Automatically update value
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

  submit(){
    console.log("miles: ", this.miles)
    console.log("value: ", this.value)

  }

}
