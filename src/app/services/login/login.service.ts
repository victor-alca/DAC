import { Injectable } from '@angular/core';
import { Login } from '../../shared';
import { catchError, map, Observable, throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

const BASE_URL = "http://localhost:3000/login"

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  
  httpOptions = {
      observe: "response" as "response",
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
  }

  constructor(private http: HttpClient) { }

  loginUser(login: Login) : Observable<any>{
    console.log(login)
    return this.http.post<Login>(BASE_URL,
      JSON.stringify(login),
      this.httpOptions).pipe(
        map((resp: HttpResponse<Login>) => {
          if(resp != null){
            console.log(resp.body)
            return resp.body
          }else{
            return null;
          }
        }),
        catchError((err) => {
          return throwError(() => err);
        }))    
  }

}
