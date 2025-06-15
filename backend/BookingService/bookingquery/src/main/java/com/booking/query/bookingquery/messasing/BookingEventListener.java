package com.booking.query.bookingquery.messasing;

import com.booking.query.bookingquery.dtos.BookingEventDTO;
import com.booking.query.bookingquery.entity.Booking;
import com.booking.query.bookingquery.repository.BookingRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BookingEventListener {

    @Autowired
    private BookingRepository bookingRepository;

    @RabbitListener(queues = "booking.query")
    public void handleBookingEvent(Map<String, Object> event) {
        String type = (String) event.get("type");
        Map<String, Object> bookingData = (Map<String, Object>) event.get("booking");

        // Map bookingData para BookingEventDTO
        BookingEventDTO dto = mapToDTO(bookingData);

        if ("CREATED".equals(type)) {
            Booking booking = new Booking();
            applyDTOToEntity(dto, booking);
            bookingRepository.save(booking);
        } else if ("UPDATED".equals(type)) {
            Booking booking = bookingRepository.findByCode(dto.code);
            if (booking != null) {
                applyDTOToEntity(dto, booking);
                bookingRepository.save(booking);
            }
        } else if ("CANCELLED".equals(type)) {
            Booking booking = bookingRepository.findByCode(dto.code);
            if (booking != null) {
                booking.setStatusBooking(dto.statusBooking);
                bookingRepository.save(booking);
            }
        }
    }

    private BookingEventDTO mapToDTO(Map<String, Object> data) {
        BookingEventDTO dto = new BookingEventDTO();
        dto.code = (String) data.get("code");
        dto.date = data.get("date") != null ? new java.util.Date((Long) data.get("date")) : null;
        dto.originAirport = (String) data.get("originAirport");
        dto.destinationAirport = (String) data.get("destinationAirport");
        dto.totalSeats = data.get("totalSeats") != null ? ((Number) data.get("totalSeats")).intValue() : null;
        dto.statusBooking = (String) data.get("statusBooking");
        dto.moneySpent = data.get("moneySpent") != null ? ((Number) data.get("moneySpent")).intValue() : null;
        dto.milesSpent = data.get("milesSpent") != null ? ((Number) data.get("milesSpent")).intValue() : null;
        dto.clientId = data.get("clientId") != null ? ((Number) data.get("clientId")).intValue() : null;
        dto.flightCode = (String) data.get("flightCode");
        return dto;
    }

    private void applyDTOToEntity(BookingEventDTO dto, Booking booking) {
        booking.setCode(dto.code);
        booking.setDate(dto.date);
        booking.setOriginAirport(dto.originAirport != null ? dto.originAirport : null);
        booking.setDestinationAirport(dto.destinationAirport != null ? dto.destinationAirport : null);
        booking.setTotalSeats(dto.totalSeats);
        booking.setStatusBooking(dto.statusBooking);
        booking.setMoneySpent(dto.moneySpent);
        booking.setMilesSpent(dto.milesSpent);
        booking.setClientId(dto.clientId);
        booking.setFlightCode(dto.flightCode);
    }
}