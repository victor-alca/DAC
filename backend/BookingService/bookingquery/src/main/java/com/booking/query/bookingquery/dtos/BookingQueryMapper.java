package com.booking.query.bookingquery.dtos;

import com.booking.query.bookingquery.entity.Booking;

public class BookingQueryMapper {
    public static BookingQueryResponseDTO toDTO(Booking booking) {
        if (booking == null) return null;
        BookingQueryResponseDTO dto = new BookingQueryResponseDTO();
        dto.codigo = booking.getCode();
        dto.data = booking.getDate() != null ? booking.getDate().toString() : null;
        dto.valor = booking.getMoneySpent() != null ? booking.getMoneySpent().doubleValue() : null;
        dto.milhas_utilizadas = booking.getMilesSpent();
        dto.quantidade_poltronas = booking.getTotalSeats(); 
        dto.codigo_cliente = booking.getClientId();
        dto.estado = booking.getStatusBooking();
        dto.codigo_voo = booking.getFlightCode();
        dto.codigo_aeroporto_origem = booking.getOriginAirport();
        dto.codigo_aeroporto_destino = booking.getDestinationAirport();
        return dto;
    }
}