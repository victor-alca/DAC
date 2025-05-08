package main.java.com.booking.command.bookingcommand.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BOOKING_COMMAND")
@Data

public class Booking implements Serializable {
    @Id
    private String code;

    @Column(nullable = false)
    private Integer flightCode;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Double moneySpent;

    @Column(nullable = false)
    private Double milesSpent;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(Integer flightCode) {
        this.flightCode = flightCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(String moneySpent) {
        this.moneySpent = moneySpent;
    }

    public Integer getMilesSpent() {
        return milesSpent;
    }

    public void setMilesSpent(Double milesSpent) {
        this.milesSpent = milesSpent;
    }

    public Double getStatus() {
        return status;
    }

    public void setStatus(Double status) {
        this.status = status;
    }

    public Booking(String code, Date date, String flightCode, Double moneySpent, Integer milesSpent, Double status) {
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