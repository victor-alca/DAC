package com.booking.command.bookingcommand.service;

import com.booking.command.bookingcommand.dtos.BookingRequestDTO;
import com.booking.command.bookingcommand.dtos.BookingResponseDTO;
import com.booking.command.bookingcommand.dtos.BookingStatusUpdateDTO;
import com.booking.command.bookingcommand.entity.Booking;
import com.booking.command.bookingcommand.entity.BookingStatus;
import com.booking.command.bookingcommand.repository.BookingRepository;
import com.booking.command.bookingcommand.repository.BookingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class BookingCommandService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        Booking booking = new Booking();
        booking.setCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setDate(new Date());
        booking.setFlightCode(dto.codigo_voo);
        booking.setMoneySpent(dto.valor != null ? dto.valor.intValue() : null);
        booking.setMilesSpent(dto.milhas_utilizadas);
        booking.setClientId(dto.codigo_cliente);
        // Status inicial: CONFIRMADA
        BookingStatus status = bookingStatusRepository.findByCode("CONFIRMADA");
        if (status == null) {
            throw new RuntimeException("Status CONFIRMADA não encontrado");
        }
        booking.setStatus(status);

        bookingRepository.save(booking);

        return toResponseDTO(booking, dto);
    }

    public BookingResponseDTO updateBookingStatus(String codigoReserva, BookingStatusUpdateDTO dto) {
        Booking booking = bookingRepository.findById(codigoReserva)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        BookingStatus status = bookingStatusRepository.findByCode(dto.estado);
        if (status == null) {
            throw new RuntimeException("Status não encontrado: " + dto.estado);
        }
        booking.setStatus(status);
        bookingRepository.save(booking);

        return toResponseDTO(booking, null);
    }

    public BookingResponseDTO cancelBooking(String codigoReserva) {
        Booking booking = bookingRepository.findById(codigoReserva)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        BookingStatus status = bookingStatusRepository.findByCode("CANCELADA");
        if (status == null) {
            throw new RuntimeException("Status CANCELADA não encontrado");
        }
        booking.setStatus(status);
        bookingRepository.save(booking);

        return toResponseDTO(booking, null);
    }

    private BookingResponseDTO toResponseDTO(Booking booking, BookingRequestDTO dto) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.codigo = booking.getCode();
        response.data = booking.getDate() != null ? booking.getDate().toString() : null;
        response.valor = booking.getMoneySpent() != null ? booking.getMoneySpent().doubleValue() : null;
        response.milhas_utilizadas = booking.getMilesSpent();
        response.quantidade_poltronas = booking.getTotalSeats();
        response.codigo_cliente = booking.getClientId(); 
        response.estado = booking.getStatus() != null ? booking.getStatus().getCode() : null;
        response.codigo_voo = booking.getFlightCode();
        response.codigo_aeroporto_origem = booking.getOriginAirport();
        response.codigo_aeroporto_destino = booking.getDestinationAirport();
        return response;
    }
}