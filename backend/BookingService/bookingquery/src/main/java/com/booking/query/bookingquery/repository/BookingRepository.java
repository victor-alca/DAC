package com.booking.query.bookingquery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.query.bookingquery.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, String> {
    Booking findByCode(String code);
    List<Booking> findAllByClientId(Integer clientId);
}
