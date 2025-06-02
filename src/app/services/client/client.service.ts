import { Injectable } from '@angular/core';
import { Client } from '../../shared/models/client/client';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Employee } from '../../shared/models/employee/employee';

const LS_KEY = "client";
const BASE_URL = "http://localhost:3000/clientes"

@Injectable({
  providedIn: 'root'
})

export class ClientService {

  httpOptions = {
    observe: "response" as "response",
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
  }

  constructor(private http: HttpClient) { }

  getById(id: number) : Observable<Client | null>{
    return this.http.get<Client>(
      BASE_URL + "/" + id,
      this.httpOptions).pipe(
        map((resp: HttpResponse<Client>) => {
          if(resp.status==200){
            return resp.body
          }else{
            return null
          }
        }),
        catchError((err) => {
          return throwError(() => err)
        })
      )
  }

  create(client: Client): Observable<Client | null>{
    return this.http.post<Client>(BASE_URL,
      JSON.stringify(client),
      this.httpOptions).pipe(
        map((resp: HttpResponse<Client> ) => {
        if (resp != null){
          return resp.body;
        }else{
          return null;
        }
      }),
      catchError((err) => {
        return throwError(() => err);
      }))
  };

  addClientMiles(client: Client, miles: number): Observable<Client | null> {
    return this.http.put<any>(
      `${BASE_URL}/${client.code}/milhas`, 
      { miles }, 
      this.httpOptions
    ).pipe(
      map((resp: HttpResponse<any>) => {
        if (resp.status === 200) {
          return resp.body; 
        } else {
          return null;
        }
      }),
      catchError((err) => {
        console.error('Erro ao adicionar milhas:', err);
        return throwError(() => err);
      })
    );
  }

  getMilesTransactions(client: Client): Observable<any> {
    return this.http.get<any>(
      `${BASE_URL}/${client.code}/milhas`,
      this.httpOptions
    ).pipe(
      map((resp: HttpResponse<any>) => {
        if (resp.status === 200) {
          return resp.body; 
        } else {
          return null;
        }
      }),
      catchError((err) => {
        console.error('Erro ao buscar histórico de transações:', err);
        return throwError(() => err);
      })
    );
  }

  getClientBookings(client: Client){ //Confirmar implementação
  }
}
  
