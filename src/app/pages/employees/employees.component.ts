import { Component } from '@angular/core';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EmployeesModalComponent } from '../employees-modal/employees-modal.component';
import { EmployeeService } from '../../services/employee/employee.service';
import { from } from 'rxjs';
import { ConfimationModalComponent } from '../confimation-modal/confimation-modal.component';
import { EmployeeDTO } from '../../shared/dtos/employeeDTO';

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css',
})
export class EmployeesComponent {
  constructor(
    private modalService: NgbModal,
    public employeeService: EmployeeService
  ) {}

  employeeList: EmployeeDTO[] = [];

  ngOnInit() {
    this.employeeService.getAll().subscribe({
      next: (data) => (this.employeeList = data || []),
      error: (err) => console.error('Erro ao buscar funcionários:', err),
    });
  }

  openEmployeesModal(employeeToEdit: EmployeeDTO | null) {
    const modalRef = this.modalService.open(EmployeesModalComponent);
    if (employeeToEdit) {
      modalRef.componentInstance.employee = employeeToEdit;
    }
    
    modalRef.result.then((result) => {
        this.loadEmployees(); 
    }).catch(() => {
    });
  }

  loadEmployees() {
    this.employeeService.getAll().subscribe({
      next: (data) => (this.employeeList = data || []),
      error: (err) => console.error('Erro ao buscar funcionários:', err),
    });
  }

  remove(employee: EmployeeDTO) {
    const modalRef = this.modalService.open(ConfimationModalComponent);
    modalRef.componentInstance.text =
      'Tem certeza que deseja remover o funcionário?';
    modalRef.componentInstance.extraText = 'A ação vai inativa-lo.';
    modalRef.result
      .then((result) => {
        if (result) {
          this.employeeService.delete(employee.codigo).subscribe({
            next: (response) => {
              this.loadEmployees();
            },
            error: (err) => {
              console.log(err);
            },
          });
        }
      })
      .catch(() => {});
  }

  filterEmployees() {
    const filteredEmployees = this.employeeList
      // .filter((b) => b.active === true)
      // .sort((a, b) => a.name.length - b.name.length);

    this.employeeList = filteredEmployees;
  }
}
