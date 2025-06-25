import { Injectable } from '@angular/core';
import { Flight } from '../../shared/models/flight/flight.model';
import { FlightStatus } from '../../shared/models/flight/flight-status.enum';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { FlightsDTO } from '../../shared/dtos/flightDto';
import { Airport } from '../../shared/models/airport/airport.model';
import { AuthService } from '../auth/auth.service';
import { CreateFlightResponseDTO } from '../../shared/dtos/createFlightResponseDTO';
import { CreateFlightDTO } from '../../shared/dtos/createFlightDTO';

const LS_KEY = 'flights';
const BASE_URL = 'http://localhost:3000/voos';
const AIRPOT_URL = 'http://localhost:3000/aeroportos';

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  constructor(private http: HttpClient, private authService: AuthService) {}

  httpOptions = {
    observe: 'response' as const,
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

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

  getAll(): Observable<FlightsDTO | null> {
    return this.http.get<FlightsDTO>(`${BASE_URL}`, this.httpOptions).pipe(
      map((resp: HttpResponse<FlightsDTO>) => (resp.status === 200 ? resp.body : null)),
      catchError((err) => throwError(() => err))
    );
  }

  getAllByPeriod(inicio: Date, fim: Date): Observable<FlightsDTO | null> {
    return this.http
      .get<FlightsDTO>(
        `${BASE_URL}?inicio=${inicio.toISOString().slice(0, 10)}&fim=${fim.toISOString().slice(0, 10)}`,
        this.httpOptions
      )
      .pipe(
        map((resp: HttpResponse<FlightsDTO>) => (resp.status === 200 ? resp.body : null)),
        catchError((err) => throwError(() => err))
      );
  }

  getAllByOriginAndDestiny(data: Date, origem: string, destino: string): Observable<FlightsDTO | null> {
    return this.http
      .get<FlightsDTO>(
        `${BASE_URL}?data=${data.toISOString().slice(0, 10)}&origem=${origem}&destino=${destino}`,
        this.httpOptions
      )
      .pipe(
        map((resp: HttpResponse<FlightsDTO>) => (resp.status === 200 ? resp.body : null)),
        catchError((err) => throwError(() => err))
      );
  }

  getAirports(): Observable<Airport[] | null> {
    return this.http.get<Airport[]>(`${AIRPOT_URL}`, this.getHttpOptions()).pipe(
      map((resp: HttpResponse<Airport[]>) => (resp.status === 200 ? resp.body : null)),
      catchError((err) => throwError(() => err))
    );
  }

  getById(code: string): Observable<FlightsDTO | null> {
    return this.http
      .get<FlightsDTO>(`${BASE_URL}?codigo=${code}`, this.getHttpOptions())
      .pipe(
        map((resp: HttpResponse<FlightsDTO>) => (resp.status === 200 ? resp.body : null)),
        catchError((err) => throwError(() => err))
      );
  }

  create(flight: CreateFlightDTO): Observable<CreateFlightResponseDTO | null> {
    return this.http
      .post<CreateFlightResponseDTO>(BASE_URL, JSON.stringify(flight), this.getHttpOptions())
      .pipe(
        map((resp: HttpResponse<CreateFlightResponseDTO>) => (resp?.body ? resp.body : null)),
        catchError((err) => throwError(() => err))
      );
  }

  updateFlightStatus(codigo: string, estado: string): Observable<any> {
    return this.http
      .patch<any>(`${BASE_URL}/${codigo}/estado`, { estado }, this.getHttpOptions())
      .pipe(
        map((resp: HttpResponse<any>) => (resp.status === 200 && resp.body ? resp.body : null)),
        catchError((err) => throwError(() => err))
      );
  }

  delete(code: string): Observable<CreateFlightResponseDTO | null> {
    return this.http
      .delete<CreateFlightResponseDTO>(`${BASE_URL}/${code}`, this.getHttpOptions())
      .pipe(
        map((resp: HttpResponse<CreateFlightResponseDTO>) => (resp.body ? resp.body : null)),
        catchError((err) => throwError(() => err))
      );
  }

  getFlightsByParams(params: any): Observable<any> {
    const cleanParams: any = {};
    
    if (params.data) {
      cleanParams.data = params.data;
    }
    
    if (params.origem && params.origem.trim()) {
      cleanParams.origem = params.origem.trim().toUpperCase();
    }
    
    if (params.destino && params.destino.trim()) {
      cleanParams.destino = params.destino.trim().toUpperCase();
    }

    return this.http.get<any>(`${BASE_URL}`, { 
      params: cleanParams,
      ...this.httpOptions 
    });
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

  getFlightStatusNumber(status: string | FlightStatus): FlightStatus {
    switch (status) {
      case 'CONFIRMADO':
        return FlightStatus.CONFIRMED;
      case 'REALIZADO':
        return FlightStatus.REALIZED;
      case 'CANCELADO':
        return FlightStatus.CANCELED;
      default:
        return 1;
    }
  }
}
