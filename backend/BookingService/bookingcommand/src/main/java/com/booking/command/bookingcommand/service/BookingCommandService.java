package com.booking.command.bookingcommand.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.command.bookingcommand.entity.Booking;
import com.booking.command.bookingcommand.repository.BookingRepository;

@Service 
public class BookingCommandService {

    @Autowired
    private BookingRepository repository;
    
    public Booking createBooking(Booking booking) {
        return repository.save(booking);
    }
    
    public Booking updateBooking(String code, Booking booking) {
        Booking existingBooking = repository.findByCode(code).get();
        existingBooking.setDate(booking.getDate());
        existingBooking.setFlightCode(booking.getFlightCode());
        existingBooking.setMoneySpent(booking.getMoneySpent());
        existingBooking.setMilesSpent(booking.getMilesSpent());
        existingBooking.setCode(booking.getCode());

        return repository.save(existingBooking);
    }
}
