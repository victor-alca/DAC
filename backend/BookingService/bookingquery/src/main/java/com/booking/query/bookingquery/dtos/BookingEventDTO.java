package com.booking.query.bookingquery.dtos;

import java.util.Date;

public class BookingEventDTO {
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