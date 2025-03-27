import { Injectable } from '@angular/core';
import { Employee } from '../../shared/models/employee/employee';

const LS_KEY = "employee"

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  constructor() { }

  getAll(): Employee[] {
      const employees = localStorage[LS_KEY];
  
      return employees ? JSON.parse(employees) : [];
    }
  
    getById(id: number) : Employee | undefined{
      const employees = this.getAll();
  
      return employees.find(e => e.ID === id);
    }
  
    create(employee: Employee){
      const employees = this.getAll();
  
      employee.ID = (employees.length > 0 ? Math.max(...employees.map(e => e.ID)) : 0) + 1;
  
      employees.push(employee);
  
      localStorage[LS_KEY] = JSON.stringify(employees);
    }
  
    update(employee : Employee){
      const employees = this.getAll();
  
      employees.forEach( (cli, ind, objs) => {
        if (employee.ID === cli.ID) {
          objs[ind] = employee
        }
      })
  
      localStorage[LS_KEY] = JSON.stringify(employee)
    }
  
    delete(id: number){
      var employees = this.getAll();
  
      employees = employees.filter(e => e.ID !== id);
  
      localStorage[LS_KEY] = JSON.stringify(employees);
    }
}
