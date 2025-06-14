package com.booking.query.bookingquery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.query.bookingquery.entity.Booking;
import com.booking.query.bookingquery.repository.BookingRepository;

@Service
public class BookingQueryService {

    @Autowired
    private BookingRepository repository;

    public List<Booking> getBookingsByCodes(List<String> codes){
        return repository.findAllById(codes);
    }

    public Booking getBookingByCode(String code) {
        return repository.findByCode(code);
    }

    public List<Booking> getBookingsByClientId(Integer clientId) {
    return repository.findAllByClientId(clientId);
}
}