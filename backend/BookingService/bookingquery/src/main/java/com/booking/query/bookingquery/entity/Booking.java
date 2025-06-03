package com.booking.query.bookingquery.entity;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "booking", schema = "bookingquery")
public class Booking implements Serializable {
    @Id
    private String code;

    @Column(name = "date")
    private Date date;

    @Column(name = "origin_airport")
    private String originAirport;

    @Column(name = "destination_airport")
    private String destinationAirport;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "statusFlight")
    private String statusFlight;

    @Column(name = "statusBooking")
    private String statusBooking;

    @Column(name = "money_spent")
    private Integer moneySpent;

    @Column(name = "miles_spent")
    private Integer milesSpent;
    
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "flight_code")
    private String flightCode;

    // getters e setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getStatusFlight() {
        return statusFlight;
    }

    public void setStatusFlight(String statusFlight) {
        this.statusFlight = statusFlight;
    }

    public String getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(String statusBooking) {
        this.statusBooking = statusBooking;
    }

    public Integer getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(Integer moneySpent) {
        this.moneySpent = moneySpent;
    }

    public Integer getMilesSpent() {
        return milesSpent;
    }

    public void setMilesSpent(Integer milesSpent) {
        this.milesSpent = milesSpent;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }
    
}