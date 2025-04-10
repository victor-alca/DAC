import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confimation-modal',
  templateUrl: './confimation-modal.component.html',
  styleUrl: './confimation-modal.component.css'
})
export class ConfimationModalComponent {
@Input() text: string = ""
@Input() extraText: string = ""

  constructor(public activeModal: NgbActiveModal) {}

  confirmar() {
    this.activeModal.close(true); 
  }
  
  cancelar() {
    this.activeModal.dismiss();
  }
}
