package com.booking.command.bookingcommand.dtos;

import java.io.Serializable;
import java.util.Date;

// Usado para passar os dados para o banco de dados de leitura
public class BookingEventDTO implements Serializable {
    public String code;
    public Date date;
    public String originAirport;
    public String destinationAirport;
    public Integer totalSeats;
    public String statusBooking;
    public Integer moneySpent;
    public Integer milesSpent;
    public Integer clientId;
    public String flightCode;
}