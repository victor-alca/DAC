package com.booking.query.bookingquery.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "BOOKING_QUERY")

public class Booking implements Serializable {
    @Id
    private String code;

    @Column(nullable = false)
    private String flightCode;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Double moneySpent;

    @Column(nullable = false)
    private Double milesSpent;

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

    public Double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(Double moneySpent) {
        this.moneySpent = moneySpent;
    }

    public Double getMilesSpent() {
        return milesSpent;
    }

    public void setMilesSpent(Double milesSpent) {
        this.milesSpent = milesSpent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Booking(String code, Date date, String flightCode, Double moneySpent, Double milesSpent, Integer status) {
        this.code = code;
        this.date = date;
        this.flightCode = flightCode;
        this.moneySpent = moneySpent;
        this.milesSpent = milesSpent;
        this.status = status;
    }

    public Booking() {
    }
}