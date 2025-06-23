import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NgForm } from '@angular/forms';
import { EmployeeService } from '../../services/employee/employee.service';
import { EmployeeDTO } from '../../shared/dtos/employeeDTO';

@Component({
  selector: 'app-employees-modal',
  templateUrl: './employees-modal.component.html',
  styleUrl: './employees-modal.component.css',
})
export class EmployeesModalComponent {
  @Input() employee!: EmployeeDTO;
  constructor(
    public activeModal: NgbActiveModal,
    public employeeService: EmployeeService
  ) {}

  edit = true;

  ngOnInit() {
    if (!this.employee) {
      this.employee = new EmployeeDTO(1,'', '', '', '');
      console.log('Criando');
      this.edit = false;
    }
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      console.log('Dados do Formulário:', form.value);
      if (this.edit) {
        this.editEmployee(form.value);
      } else {
        this.createEmployee(form.value);
      }
    } else {
      console.log('Formulário inválido');
    }
  }

  editEmployee(EmployeeDTO: EmployeeDTO) {
    EmployeeDTO.codigo = this.employee.codigo;
    // EmployeeDTO.active = this.employee.active;
    this.employeeService.update(EmployeeDTO).subscribe({
      next: (response) => {
        this.activeModal.close('updated');
        alert('Funcionário atualizado com sucesso!');
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  createEmployee(EmployeeDTO: EmployeeDTO) {
    // EmployeeDTO.active = true;
    this.employeeService.create(EmployeeDTO).subscribe({
      next: (response) => {
        this.activeModal.close('created');
        alert('Senha enviada para o email do novo funcionário');
      },
      error: (err) => {
        console.log(err);
      },
    });
  }
}
