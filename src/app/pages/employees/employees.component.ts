import { Component } from '@angular/core';
import { Employee } from '../../shared/models/employee/employee';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EmployeesModalComponent } from '../employees-modal/employees-modal.component';
import { EmployeeService } from '../../services/employee/employee.service';
import { from } from 'rxjs';
import { ConfimationModalComponent } from '../confimation-modal/confimation-modal.component';

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})
export class EmployeesComponent {

  constructor(private modalService: NgbModal, public employeeService: EmployeeService) {}

  employeeList: Employee[] = []

  ngOnInit(){
    this.employeeService.seedEmployees();
    this.loadList()
  }

  openEmployeesModal(employeeToEdit: Employee | null){
    const modalRef = this.modalService.open(EmployeesModalComponent);
    if(employeeToEdit){
      modalRef.componentInstance.employee = employeeToEdit;
    }
    modalRef.result.then(() => {
      this.loadList()
    }).catch(() => {});
  }

  remove(employee: Employee){
    const modalRef = this.modalService.open(ConfimationModalComponent);
    modalRef.componentInstance.text = "Tem certeza que deseja remover o funcionário?"
    modalRef.componentInstance.extraText = "A ação vai inativa-lo."
    modalRef.result.then((result) => {
      if(result){
        employee.active = false;
        this.employeeService.update(employee)
        this.loadList()
      }
    }).catch(() => {});
  }

  loadList(){
    this.employeeList = this.employeeService.getAll();
    console.log(this.employeeList);
    this.filterEmployees();
    console.log(this.employeeList);
  }

  filterEmployees() {
    const filteredEmployees = this.employeeList
        .filter(b => 
            b.active === true
        )
        .sort((a, b) => a.name.length - b.name.length);

    this.employeeList = filteredEmployees;
  }
}
