import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';

import { AuthService } from '../auth/auth.service';
import { EmployeeDTO } from '../../shared/dtos/employeeDTO';

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private apiUrl = 'http://localhost:3000/funcionarios';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getHttpOptions() {
    const token = this.authService.getAccessToken();
    return {
      observe: 'response' as const,
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      }),
    };
  }

  getAll(): Observable<EmployeeDTO[] | null> {
    return this.http.get<EmployeeDTO[]>(this.apiUrl, this.getHttpOptions()).pipe(
      map((resp: HttpResponse<EmployeeDTO[]>) => {
        if (resp.status == 200) {
          console.log(resp.body);
          return resp.body;
        } else {
          return null;
        }
      }),
      catchError((err) => {
        return throwError(() => err);
      })
    );
  }

  getById(codigo: number): Observable<EmployeeDTO | null> {
    return this.http
      .get<EmployeeDTO>(`${this.apiUrl}/${codigo}`, this.getHttpOptions())
      .pipe(
        map((resp: HttpResponse<EmployeeDTO>) => {
          if (resp.status == 200) {
            console.log(resp.body);
            return resp.body;
          } else {
            return null;
          }
        }),
        catchError((err) => {
          return throwError(() => err);
        })
      );
  }

  create(employee: EmployeeDTO): Observable<EmployeeDTO | null> {
    return this.http
      .post<EmployeeDTO>(this.apiUrl, employee, this.getHttpOptions())
      .pipe(
        map((resp: HttpResponse<EmployeeDTO>) => {
          if (resp.status == 200) {
            console.log(resp.body);
            return resp.body;
          } else {
            return null;
          }
        }),
        catchError((err) => {
          return throwError(() => err);
        })
      );
  }

  update(employee: EmployeeDTO): Observable<EmployeeDTO | null> {
    return this.http
      .put<EmployeeDTO>(
        `${this.apiUrl}/${employee.codigo}`,
        employee,
        this.getHttpOptions()
      )
      .pipe(
        map((resp: HttpResponse<EmployeeDTO>) => {
          if (resp.status == 200) {
            console.log(resp.body);
            return resp.body;
          } else {
            return null;
          }
        }),
        catchError((err) => {
          return throwError(() => err);
        })
      );
  }

  delete(id: number): Observable<EmployeeDTO | null> {
    return this.http
      .delete<EmployeeDTO>(`${this.apiUrl}/${id}`, {
        ...this.getHttpOptions(),
        observe: 'response',
      })
      .pipe(
        map((resp: HttpResponse<EmployeeDTO>) => {
          if (resp.status === 200) {
            console.log(resp.body);
            return resp.body;
          } else {
            return null;
          }
        }),
        catchError((err) => throwError(() => err))
      );
  }
}
