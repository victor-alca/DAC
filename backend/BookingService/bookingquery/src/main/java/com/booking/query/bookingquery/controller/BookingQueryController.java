package com.booking.query.bookingquery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.query.bookingquery.entity.Booking;

import com.booking.query.bookingquery.service.BookingQueryService;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/reservas")
@RestController
public class BookingQueryController {

    @Autowired
    private BookingQueryService queryService;

    @GetMapping
    public List<Booking> fetchAllBookings(){
        return queryService.getBookings();
    }
}
