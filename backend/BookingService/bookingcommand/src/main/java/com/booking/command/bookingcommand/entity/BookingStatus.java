package com.booking.command.bookingcommand.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bookingstatus", schema = "bookingcommand")
public class BookingStatus implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public BookingStatus() {}

    public BookingStatus(Integer id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}