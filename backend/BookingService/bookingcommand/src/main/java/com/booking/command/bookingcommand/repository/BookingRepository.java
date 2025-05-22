package com.booking.command.bookingcommand.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.command.bookingcommand.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking,String> {

    Booking findByCode(String code);

}
