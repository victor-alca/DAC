import { Injectable } from '@angular/core';
import { Flight } from '../shared/models/flight/flight.model';
import { FlightStatus } from '../shared/models/flight/flight-status.enum';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, pipe, throwError } from 'rxjs';
import { FlightsDTO } from '../shared/dtos/flightDto';
import { Airport } from '../shared/models/airport/airport.model';
import { AuthService } from './auth/auth.service';
import { CreateFlightResponseDTO } from '../shared/dtos/createFlightResponseDTO';
import { CreateFlightDTO } from '../shared/dtos/createFlightDTO';

// const para o local storage
const LS_KEY = 'flights';
const BASE_URL = 'http://localhost:3000/voos'
const AIRPOT_URL = 'http://localhost:3000/aeroportos'

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  constructor(private http: HttpClient, private authService: AuthService) {}

  httpOptions = {
        observe: "response" as "response",
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        }),
    }

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

  getAllByPeriod(inicio: Date, fim: Date) : Observable<FlightsDTO | null>{
      return this.http.get<FlightsDTO>(
        `${BASE_URL}?inicio=${inicio.toISOString().slice(0, 10)}&fim=${fim.toISOString().slice(0, 10)}`,
        this.httpOptions).pipe(
          map((resp: HttpResponse<FlightsDTO>) => {
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

  getAllByOriginAndDestiny(data: Date, origem: String, destino: String): Observable<FlightsDTO | null>{
    return this.http.get<FlightsDTO>(
        `${BASE_URL}?data=${data.toISOString().slice(0, 10)}&origem=${origem}&destino=${destino}`,
        this.httpOptions).pipe(
          map((resp: HttpResponse<FlightsDTO>) => {
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

  getAirports(): Observable<Airport[] | null> {
    return this.http.get<Airport[]>(
        `${AIRPOT_URL}`,
        this.getHttpOptions()).pipe(
          map((resp: HttpResponse<Airport[]>) => {
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

  getAll(): Observable<FlightsDTO | null> {
    return this.http.get<FlightsDTO>(
        `${BASE_URL}`,
        this.httpOptions).pipe(
          map((resp: HttpResponse<FlightsDTO>) => {
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

  create(flight: CreateFlightDTO): Observable<CreateFlightResponseDTO | null>{
        return this.http.post<CreateFlightResponseDTO>(BASE_URL,
          flight,
          this.getHttpOptions()).pipe(
            map((resp: HttpResponse<CreateFlightResponseDTO> ) => {
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


  getById(code: string): Observable<FlightsDTO | null> {
    return this.http.get<FlightsDTO>(
        `${BASE_URL}?codigo=${code}`,
        this.getHttpOptions()).pipe(
          map((resp: HttpResponse<FlightsDTO>) => {
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

  update(flight: CreateFlightDTO): Observable<CreateFlightResponseDTO | null> {
  return this.http.put<CreateFlightResponseDTO>(`${BASE_URL}`,
        this.getHttpOptions())
  }

  delete(code: string): Observable<CreateFlightResponseDTO | null> {
    return this.http.delete<CreateFlightResponseDTO>(`${BASE_URL}/${code}`,
        this.getHttpOptions()).pipe(map((resp: HttpResponse<CreateFlightResponseDTO>) => {
            if (resp.body) {
              console.log(resp.body)
              return resp.body;
            } else {
              return null;
            }
          }),
          catchError((err) => throwError(() => err))
        );
  }
  // Método temporário para inserir voos manualmente
  // seedFlights(): void {
  //   const now = new Date();
  //   const next48Hours = new Date();
  //   next48Hours.setHours(now.getHours() + 48);
  
  //   const flights: Flight[] = [
  //     // Voo 1: Dentro das próximas 48 horas
  //     new Flight(
  //       '1',
  //       new Date(now.getTime() + 2 * 60 * 60 * 1000), // 2 horas a partir de agora
  //       'CWB',
  //       'GRU',
  //       500,
  //       180,
  //       50,
  //       FlightStatus.CONFIRMED
  //     ),
  //     // Voo 2: Dentro das próximas 48 horas
  //     new Flight(
  //       '2',
  //       new Date(now.getTime() + 24 * 60 * 60 * 1000), // 24 horas a partir de agora
  //       'GRU',
  //       'GIG',
  //       400,
  //       200,
  //       120,
  //       FlightStatus.CONFIRMED
  //     ),
  //     // Voo 3: Fora das próximas 48 horas
  //     new Flight(
  //       '3',
  //       new Date(next48Hours.getTime() + 24 * 60 * 60 * 1000), // 72 horas a partir de agora
  //       'BSB',
  //       'POA',
  //       300,
  //       150,
  //       80,
  //       FlightStatus.CANCELED
  //     ),
  //   ];
  
  //   localStorage[LS_KEY] = JSON.stringify(flights);
  // }

  getFlightStatusText(status: FlightStatus): string {
    switch (status) {
      case FlightStatus.CONFIRMED:
        return 'Confirmado';
      case FlightStatus.CANCELED:
        return 'Cancelado';
      case FlightStatus.REALIZED:
        return 'Realizado';
      default:
        return 'Desconhecido';
    }
  }
}
