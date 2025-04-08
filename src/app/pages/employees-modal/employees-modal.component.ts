import { Component, Input } from '@angular/core';
import { Employee } from '../../shared/models/employee/employee';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NgForm } from '@angular/forms';
import { EmployeeService } from '../../services/employee/employee.service';

@Component({
  selector: 'app-employees-modal',
  templateUrl: './employees-modal.component.html',
  styleUrl: './employees-modal.component.css'
})
export class EmployeesModalComponent {
  @Input() employee!: Employee
  constructor(public activeModal: NgbActiveModal, public employeeService: EmployeeService) {}

  edit = true;

  ngOnInit(){
    if(!this.employee){
      this.employee = new Employee(1, true, "", "", "", "")
      this.edit = false;
    }
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      console.log('Dados do Formulário:', form.value);
      if(this.edit){
        this.editEmployee(form.value)
      }else{
        this.createEmployee(form.value);
      }
    } else {
      console.log('Formulário inválido');
    }
  }

  editEmployee(Employee: Employee){
    Employee.ID = this.employee.ID;
    Employee.active = this.employee.active;
    this.employeeService.update(Employee);
    this.activeModal.close();
  }

  createEmployee(Employee: Employee){
    Employee.active = true;
    this.employeeService.create(Employee);
    this.activeModal.close();
  }
}
