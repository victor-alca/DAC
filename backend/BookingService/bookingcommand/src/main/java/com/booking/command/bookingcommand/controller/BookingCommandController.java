package com.booking.command.bookingcommand.controller;

import org.springframework.web.bind.annotation.RestController;

import com.booking.command.bookingcommand.entity.Booking;
import com.booking.command.bookingcommand.service.BookingCommandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/reservas")
public class BookingCommandController {

    @Autowired
    private BookingCommandService commandService;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return commandService.createBooking(booking);
    }

    @PutMapping("/{code}")
    public Booking updateBooking(@PathVariable String code, @RequestBody Booking booking) {
        return commandService.updateBooking(code, booking);
    }
}
