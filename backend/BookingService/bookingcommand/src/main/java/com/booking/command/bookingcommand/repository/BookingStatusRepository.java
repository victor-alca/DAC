package com.booking.command.bookingcommand.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.command.bookingcommand.entity.BookingStatus;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Integer> {
	
    BookingStatus findByCode(String code);
    BookingStatus findByCodeIgnoreCase(String code);

}
