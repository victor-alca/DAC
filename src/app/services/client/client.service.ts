import { Injectable } from '@angular/core';
import { ClientDTO } from '../../shared/dtos/clientDto';
import { Client} from '../../shared/models/client/client'
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { CreateBookingResponseDTO } from '../../shared/dtos/createBookingResponseDTO';

const BASE_URL = "http://localhost:3000/clientes"

@Injectable({
  providedIn: 'root'
})

export class ClientService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  getHttpOptions() {
    const token = this.authService.getAccessToken();
    return {
      observe: "response" as const,
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      })
    };
  }

  getById(id: number) : Observable<ClientDTO | null>{
    return this.http.get<ClientDTO>(
      BASE_URL + "/" + id,
      this.getHttpOptions()).pipe(
        map((resp: HttpResponse<ClientDTO>) => {
          if(resp.status==200){
            console.log(resp.body)
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

  create(client: ClientDTO): Observable<Client | null>{
    return this.http.post<Client>(BASE_URL,
      JSON.stringify(client),
      this.getHttpOptions()).pipe(
        map((resp: HttpResponse<Client> ) => {
        if (resp != null){
          console.log(resp.body)
          return resp.body;
        }else{
          return null;
        }
      }),
      catchError((err) => {
        return throwError(() => err);
      }))
  };

  addClientMiles(code: number, miles: number): Observable<Client | null> {
    return this.http.put<any>(
      `${BASE_URL}/${code}/milhas`, 
      { 'quantidade': miles }, 
      this.getHttpOptions()
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
      this.getHttpOptions()
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

  getClientBookings(code: number) : Observable<CreateBookingResponseDTO[] | null>{
    return this.http.get<CreateBookingResponseDTO[]>(
      `${BASE_URL}/${code}/reservas`,
      this.getHttpOptions()).pipe(
        map((resp: HttpResponse<CreateBookingResponseDTO[]>) => {
          if(resp.status==200){
            console.log(resp.body)
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
}

