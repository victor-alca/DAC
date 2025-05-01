import { Component } from '@angular/core';
import { Employee } from '../../shared/models/employee/employee';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EmployeesModalComponent } from '../employees-modal/employees-modal.component';
import { EmployeeService } from '../../services/employee/employee.service';
import { from } from 'rxjs';
import { ConfimationModalComponent } from '../confimation-modal/confimation-modal.component';
import { Injectable } from '@angular/core';


@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})

@Injectable({
  providedIn: 'root'
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


// teste backend
// algumas funcoes para serem usadas no futuro

loadEmployees(): void {
  this.employeeService.getAllHttp().subscribe({
    next: (data) => {
      console.log('Funcionários:', data);
    },
    error: (err) => console.error('Erro ao carregar funcionários', err)
  });
}

removeb(emp: Employee): void {
  if (confirm(`Deseja remover ${emp.name}?`)) {
    this.employeeService.deleteHttp(emp.id).subscribe({
      next: () => {
        console.log(`${emp.name} removido.`);
        this.loadEmployees();
      },
      error: (err) => console.error('Erro ao remover', err)
    });
  }
}

openEmployeesModalb(emp: Employee | null): void {
  const modalRef = this.modalService.open(EmployeesModalComponent);
  if (emp) {
    modalRef.componentInstance.employee = emp;
  }
  modalRef.result.then(() => {
    this.loadEmployees();
  }).catch(() => {});
}
