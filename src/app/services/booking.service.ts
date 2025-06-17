import { Injectable } from '@angular/core';
import { Booking } from '../shared/models/booking/booking.model';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';
import { AuthService } from './auth/auth.service';
import { BookingStatus } from '../shared/models/booking/booking-status.enum';
import { Flight } from '../shared/models/flight/flight.model';

const BASE_URL = "http://localhost:3000/reservas";

@Injectable({
  providedIn: 'root',
})
export class BookingService {

  constructor(private http: HttpClient, private authService: AuthService) {}

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

  getAll(): Observable<Booking[]> {
    return this.http.get<Booking[]>(BASE_URL, this.getHttpOptions()).pipe(
      map((resp: HttpResponse<Booking[]>) => resp.body || []),
      catchError(err => throwError(() => err))
    );
  }

  getById(id: number): Observable<Booking | null> {
    return this.http.get<Booking>(`${BASE_URL}/${id}`, this.getHttpOptions()).pipe(
      map((resp: HttpResponse<Booking>) => resp.status === 200 ? resp.body : null),
      catchError(err => throwError(() => err))
    );
  }

  create(booking: Booking): Observable<Booking | null> {
    return this.http.post<Booking>(BASE_URL, JSON.stringify(booking), this.getHttpOptions()).pipe(
      map((resp: HttpResponse<Booking>) => resp.status === 201 ? resp.body : null),
      catchError(err => throwError(() => err))
    );
  }

  update(booking: Booking): Observable<Booking | null> {
    return this.http.put<Booking>(`${BASE_URL}/${booking.ID}`, JSON.stringify(booking), this.getHttpOptions()).pipe(
      map((resp: HttpResponse<Booking>) => resp.status === 200 ? resp.body : null),
      catchError(err => throwError(() => err))
    );
  }

  delete(id: number): Observable<boolean> {
    return this.http.delete(`${BASE_URL}/${id}`, this.getHttpOptions()).pipe(
      map((resp: HttpResponse<any>) => resp.status === 204),
      catchError(err => throwError(() => err))
    );
  }

  getActiveBookings(): Observable<Booking[]> {
    return this.getAll().pipe(
      map(bookings => bookings.filter(b => b.status === BookingStatus.CREATED || b.status === BookingStatus.CHECK_IN))
    );
  }

  getLastFlights(): Observable<Booking[]> {
    return this.getAll().pipe(
      map(bookings => bookings.filter(b => b.status === BookingStatus.REALIZED || b.status === BookingStatus.NOT_REALIZED))
    );
  }

  getBookingStatusText(status: BookingStatus): string {
    switch (status) {
      case BookingStatus.CREATED:
        return 'Criada';
      case BookingStatus.CHECK_IN:
        return 'Check-In Realizado';
      case BookingStatus.CANCELED:
        return 'Cancelada';
      case BookingStatus.FLIGHT_CANCELED:
        return 'Voo Cancelado';
      case BookingStatus.SHIPPED:
        return 'Embarcado';
      case BookingStatus.REALIZED:
        return 'Realizada';
      case BookingStatus.NOT_REALIZED:
        return 'Não Realizada';
      default:
        return 'Desconhecido';
    }
  }

  getFlightDetails(flightId: string): Observable<Flight | null> {
    const url = `http://localhost:3000/voos/${flightId}`;
    return this.http.get<Flight>(url, this.getHttpOptions()).pipe(
      map((resp: HttpResponse<Flight>) => resp.status === 200 ? resp.body : null),
      catchError(err => throwError(() => err))
    );
  }
}
