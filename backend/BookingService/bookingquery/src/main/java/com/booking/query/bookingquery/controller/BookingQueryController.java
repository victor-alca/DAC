package com.booking.query.bookingquery.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booking.query.bookingquery.entity.Booking;
import com.booking.query.bookingquery.service.BookingQueryService;
import com.booking.query.bookingquery.dtos.BookingQueryResponseDTO;
import com.booking.query.bookingquery.dtos.BookingQueryMapper;

@RestController
@RequestMapping("/reservas")
public class BookingQueryController {

    @Autowired
    private BookingQueryService queryService;

    // Buscar uma reserva específica
    @GetMapping("/{codigoReserva}")
    public ResponseEntity<BookingQueryResponseDTO> fetchBookingByCode(@PathVariable String codigoReserva){
        Booking booking = queryService.getBookingByCode(codigoReserva);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BookingQueryMapper.toDTO(booking));
    }

    // Buscar reservas por cliente
    @GetMapping("/clientes/{codigoCliente}/reservas")
    public ResponseEntity<List<BookingQueryResponseDTO>> fetchBookingsByClient(@PathVariable Integer codigoCliente){
        List<Booking> bookings = queryService.getBookingsByClientId(codigoCliente);
        if (bookings == null || bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<BookingQueryResponseDTO> dtos = bookings.stream()
            .map(BookingQueryMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}