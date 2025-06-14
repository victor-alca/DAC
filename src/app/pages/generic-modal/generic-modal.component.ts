import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-generic-modal',
  templateUrl: './generic-modal.component.html',
  styleUrl: './generic-modal.component.css'
})
export class GenericModalComponent {
@Input() title: string = ""
@Input() text: string = ""

  constructor(public activeModal: NgbActiveModal) {}
}

