import { Injectable } from '@angular/core';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { FlightsDTO } from '../../shared/dtos/flightDto';

// const para o local storage
const LS_KEY = 'flights';
const BASE_URL = 'http://localhost:3000/voos'

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  constructor(private http: HttpClient) {}

  httpOptions = {
        observe: "response" as "response",
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        }),
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


  getAll(): Flight[] {
    const flights = localStorage[LS_KEY];
    return flights ? JSON.parse(flights).map((flight: any) => ({
      ...flight,
      date: new Date(flight.date) // Converte strings de data para objetos Date
    })) : [];
  }

  update(flight: Flight): void {
    const flights = this.getAll();

    flights.forEach((obj, index, objs) => {
      if (flight.codigo === obj.codigo) {
        objs[index] = flight;
      }
    });

    localStorage[LS_KEY] = JSON.stringify(flights);
  }


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
