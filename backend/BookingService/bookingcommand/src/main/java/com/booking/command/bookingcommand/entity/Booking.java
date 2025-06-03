package com.booking.command.bookingcommand.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "booking", schema = "bookingcommand")
public class Booking implements Serializable {
    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "flight_code", nullable = false)
    private String flightCode;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "money_spent")
    private Integer moneySpent;

    @Column(name = "miles_spent")
    private Integer milesSpent;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "origin_airport", nullable = false)
    private String originAirport;

    @Column(name = "destination_airport", nullable = false)
    private String destinationAirport;

    public Booking() {}

    public Booking(String code, Date date, String flightCode, Integer moneySpent, Integer milesSpent, BookingStatus status,
                   Integer clientId, Integer totalSeats, String originAirport, String destinationAirport) {
        this.code = code;
        this.date = date;
        this.flightCode = flightCode;
        this.moneySpent = moneySpent;
        this.milesSpent = milesSpent;
        this.status = status;
        this.clientId = clientId;
        this.totalSeats = totalSeats;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
    }

    // getters e setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
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

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
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
    
}
