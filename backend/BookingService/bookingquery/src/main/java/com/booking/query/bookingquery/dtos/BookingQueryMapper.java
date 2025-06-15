package com.booking.query.bookingquery.dtos;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.booking.query.bookingquery.entity.Booking;

public class BookingQueryMapper {
    public static BookingQueryResponseDTO toDTO(Booking booking) {
        if (booking == null) return null;
        BookingQueryResponseDTO dto = new BookingQueryResponseDTO();
        dto.codigo = booking.getCode();

        if (booking.getDate() != null) {
            ZonedDateTime zdt = booking.getDate().toInstant()
                .atZone(ZoneId.of("America/Sao_Paulo"));
            dto.data = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            dto.data = null;
        }      
         
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