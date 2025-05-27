import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee } from '../../shared/models/employee/employee';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:5000/funcionarios';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem('access_token') 
    });
  }

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getById(codigo: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/${codigo}`, { headers: this.getAuthHeaders() });
  }

  create(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee, { headers: this.getAuthHeaders() });
  }

  update(employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${employee.ID}`, employee, { headers: this.getAuthHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }
}
