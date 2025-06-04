package com.booking.command.bookingcommand.controller;

import com.booking.command.bookingcommand.dtos.BookingRequestDTO;
import com.booking.command.bookingcommand.dtos.BookingResponseDTO;
import com.booking.command.bookingcommand.dtos.BookingStatusUpdateDTO;
import com.booking.command.bookingcommand.service.BookingCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservas")
public class BookingCommandController {

    @Autowired
    private BookingCommandService commandService;

    // Criar reserva
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO dto) {
        try {
            BookingResponseDTO response = commandService.createBooking(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Atualizar status da reserva (PATCH)
    @PatchMapping("/{codigoReserva}/estado")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(@PathVariable String codigoReserva, @RequestBody BookingStatusUpdateDTO dto) {
        BookingResponseDTO response = commandService.updateBookingStatus(codigoReserva, dto);
        return ResponseEntity.ok(response);
    }

    // Cancelar reserva (DELETE)
    @DeleteMapping("/{codigoReserva}")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable String codigoReserva) {
        BookingResponseDTO response = commandService.cancelBooking(codigoReserva);
        return ResponseEntity.ok(response);
    }
}