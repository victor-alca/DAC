package com.flight.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "flight", schema = "flight")
public class Flight implements Serializable {
    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "origin_airport", nullable = false)
    private String originAirport;

    @Column(name = "destination_airport", nullable = false)
    private String destinationAirport;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "occupated_seats")
    private Integer occupatedSeats;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "valor_passagem")
    private Double valorPassagem;

    // Construtores
    public Flight() {}

    public Flight(String code, LocalDateTime date, String originAirport, String destinationAirport, Integer totalSeats, Integer occupatedSeats, Integer status) {
        this.code = code;
        this.date = date;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.totalSeats = totalSeats;
        this.occupatedSeats = occupatedSeats;
        this.status = status;
    }

    // Getters e Setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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

    public Integer getOccupatedSeats() {
        return occupatedSeats;
    }

    public void setOccupatedSeats(Integer occupatedSeats) {
        this.occupatedSeats = occupatedSeats;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(Double valorPassagem) {
        this.valorPassagem = valorPassagem;
    }
}