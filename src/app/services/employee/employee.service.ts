import { Injectable } from '@angular/core';
import { Employee } from '../../shared/models/employee/employee';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private baseUrl = 'http://localhost:8080/employees';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.baseUrl);
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/${id}`);
  }

  getByEmail(email: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/email/${email}`);
  }

  getByActive(active: boolean): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.baseUrl}/active/${active}`);
  }

  create(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.baseUrl, employee);
  }

  update(employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(this.baseUrl, employee);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }



/* VERSÃO ANTIGA
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
      console.log(employee);
      
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
  
      localStorage[LS_KEY] = JSON.stringify(employees);
    }
  
    delete(id: number){
      var employees = this.getAll();
  
      employees = employees.filter(e => e.ID !== id);
  
      localStorage[LS_KEY] = JSON.stringify(employees);
    }

    seedEmployees(): void {
      const employees: Employee[] = [
        new Employee(1, true, '123.456.789-00', 'João Silva', 'joao@email.com', '1234567899'),
        new Employee(2, true, '987.654.321-00', 'Maria Oliveira', 'maria@email.com', '9876543211'),
      ];
      localStorage[LS_KEY] = JSON.stringify(employees);
    }
}

*/

}
