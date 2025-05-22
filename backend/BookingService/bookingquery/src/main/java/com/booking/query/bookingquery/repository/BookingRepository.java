package com.booking.query.bookingquery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.query.bookingquery.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking,String> {

    Object findByCode(String code);

}
