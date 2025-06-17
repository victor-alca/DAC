package com.booking.command.bookingcommand.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.command.bookingcommand.entity.Booking;
import com.booking.command.bookingcommand.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking,String> {

    Booking findByCode(String code);
    Optional<Booking> findByClientIdAndFlightCodeAndTotalSeats(Integer clientId, String flightCode, Integer totalSeats);
    List<Booking> findByFlightCodeAndStatus(String flightCode, BookingStatus status);
    List<Booking> findByFlightCode(String flightCode);
}
