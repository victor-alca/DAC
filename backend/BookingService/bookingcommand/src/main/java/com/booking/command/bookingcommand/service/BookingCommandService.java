package com.booking.command.bookingcommand.service;

import com.booking.command.bookingcommand.config.RabbitConfig;
import com.booking.command.bookingcommand.dtos.BookingEventDTO;
import com.booking.command.bookingcommand.dtos.BookingRequestDTO;
import com.booking.command.bookingcommand.dtos.BookingResponseDTO;
import com.booking.command.bookingcommand.dtos.BookingStatusUpdateDTO;
import com.booking.command.bookingcommand.dtos.ReservationDTO;
import com.booking.command.bookingcommand.entity.Booking;
import com.booking.command.bookingcommand.entity.BookingStatus;
import com.booking.command.bookingcommand.repository.BookingRepository;
import com.booking.command.bookingcommand.repository.BookingStatusRepository;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class BookingCommandService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private BookingEventDTO toEventDTO(Booking booking) {
        BookingEventDTO dto = new BookingEventDTO();
        dto.code = booking.getCode();
        dto.date = booking.getDate();
        dto.originAirport = booking.getOriginAirport();
        dto.destinationAirport = booking.getDestinationAirport();
        dto.totalSeats = booking.getTotalSeats();
        dto.statusBooking = booking.getStatus() != null ? booking.getStatus().getCode() : null;
        dto.moneySpent = booking.getMoneySpent();
        dto.milesSpent = booking.getMilesSpent();
        dto.clientId = booking.getClientId();
        dto.flightCode = booking.getFlightCode();
        return dto;
    }

    public void publishBookingEvent(String eventType, Booking booking) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", eventType);
        event.put("booking", toEventDTO(booking));
        amqpTemplate.convertAndSend(RabbitConfig.BOOKING_EXCHANGE, "booking." + eventType.toLowerCase(), event);
    }

    private String generateUniqueBookingCode() {
        Random random = new Random();
        
        // Gera 3 letras maiúsculas
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            letters.append(letter);
        }
        
        // Gera 3 números
        String numbers = String.format("%03d", random.nextInt(1000));
        
        return letters.toString() + numbers;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        Booking booking = new Booking();
        booking.setCode(generateUniqueBookingCode()); 
        booking.setDate(Timestamp.from(Instant.now()));
        booking.setFlightCode(dto.codigo_voo);
        booking.setMoneySpent(dto.valor != null ? dto.valor.intValue() : null);
        booking.setMilesSpent(dto.milhas_utilizadas);
        booking.setClientId(dto.codigo_cliente);
        booking.setOriginAirport(dto.codigo_aeroporto_origem);
        booking.setDestinationAirport(dto.codigo_aeroporto_destino);
        booking.setTotalSeats(dto.quantidade_poltronas);
        // Status inicial: CRIADA
        BookingStatus status = bookingStatusRepository.findByCode("CRIADA");
        if (status == null) {
            throw new RuntimeException("Status CRIADA não encontrado");
        }
        booking.setStatus(status);

        bookingRepository.save(booking);
        publishBookingEvent("CREATED", booking);

        return toResponseDTO(booking, dto);
    }

    public String createBookingBySaga(ReservationDTO dto) {
        try {
            // Converte o ReservationDTO para BookingRequestDTO
            BookingRequestDTO request = new BookingRequestDTO();
            request.codigo_cliente = dto.getCodigo_cliente();
            request.valor = dto.getValor();
            request.milhas_utilizadas = dto.getMilhas_utilizadas();
            request.quantidade_poltronas = dto.getQuantidade_poltronas();
            request.codigo_voo = dto.getCodigo_voo();
            request.codigo_aeroporto_origem = dto.getCodigo_aeroporto_origem();
            request.codigo_aeroporto_destino = dto.getCodigo_aeroporto_destino();

            // Usa o método padrão com CQRS e retorna o código da reserva
            BookingResponseDTO response = createBooking(request);
            return response.codigo; // Retorna o código da reserva criada
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar reserva na SAGA");
        }
    }

    public boolean criarReservaSaga(ReservationDTO dto) {
        try {
            createBookingBySaga(dto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cancelBookingBySaga(ReservationDTO dto) {
        // Cancela a reserva do cliente para o voo específico
        Booking booking = bookingRepository
            .findByClientIdAndFlightCodeAndTotalSeats(
                dto.getCodigo_cliente(),
                dto.getCodigo_voo(),
                dto.getQuantidade_poltronas()
            )
            .stream()
            .findFirst()
            .orElse(null);

        if (booking != null) {
            BookingStatus status = bookingStatusRepository.findByCode("CANCELADA");
            if (status != null) {
                booking.setStatus(status);
                bookingRepository.save(booking);
                publishBookingEvent("CANCELLED", booking);
                System.out.println("[RESERVA] Reserva cancelada por compensação SAGA.");
            }
        } else {
            System.out.println("[RESERVA] Nenhuma reserva encontrada para compensação SAGA.");
        }
    }

    public BookingResponseDTO updateBookingStatus(String codigoReserva, BookingStatusUpdateDTO dto) {
        System.out.println("Recebido estado: " + dto.estado);
        Booking booking = bookingRepository.findById(codigoReserva)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        BookingStatus status = bookingStatusRepository.findByCodeIgnoreCase(dto.estado);
        if (status == null) {
            throw new RuntimeException("Status não encontrado: " + dto.estado);
        }
        booking.setStatus(status);
        bookingRepository.save(booking);
        publishBookingEvent("UPDATED", booking);

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
        publishBookingEvent("CANCELLED", booking);

        return toResponseDTO(booking, null);
    }

    private BookingResponseDTO toResponseDTO(Booking booking, BookingRequestDTO dto) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.codigo = booking.getCode();

        // Formatar data para ISO 8601 com timezone de São Paulo
        if (booking.getDate() != null) {
            ZonedDateTime zdt = booking.getDate().toInstant()
                .atZone(ZoneId.of("America/Sao_Paulo"));
            response.data = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            response.data = null;
        }    
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