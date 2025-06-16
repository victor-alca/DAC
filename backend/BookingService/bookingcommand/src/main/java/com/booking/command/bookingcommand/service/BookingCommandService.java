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
        
        System.out.println("Reserva encontrada: " + booking.getCode());
        
        BookingStatus status = bookingStatusRepository.findByCodeIgnoreCase(dto.estado);
        
        System.out.println("Status encontrado: " + (status != null ? status.getCode() : "NULL"));
        
        if (status == null) {
            throw new RuntimeException("Status não encontrado: " + dto.estado);
        }
        
        try {
            System.out.println("Tentando setar status...");
            booking.setStatus(status);
            System.out.println("Status setado com sucesso, tentando salvar...");
            bookingRepository.save(booking);
            System.out.println("Salvou com sucesso, publicando evento...");
            publishBookingEvent("UPDATED", booking);
            System.out.println("Evento publicado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro no processo: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return toResponseDTO(booking, null);
    }

    public boolean cancelBooking(ReservationDTO dto) {
        try {
            String codigoReserva = dto.getCodigo_reserva();
            
            // Busca a reserva pelo código
            Booking booking = bookingRepository.findById(codigoReserva).orElse(null);
            if (booking == null) {
                throw new RuntimeException("Reserva não encontrada: " + codigoReserva);
            }

            // Armazena o status anterior para possível compensação
            String statusAnterior = booking.getStatus().getCode();
            
            // Verifica se o status permite cancelamento (CRIADA ou CHECK-IN)
            if (!"CRIADA".equals(statusAnterior) && !"CHECK-IN".equals(statusAnterior)) {
                throw new RuntimeException("Reserva não pode ser cancelada. Status atual: " + statusAnterior);
            }

            // Preenche os dados da reserva no DTO para os próximos passos da saga
            dto.setCodigo_cliente(booking.getClientId());
            dto.setMilhas_utilizadas(booking.getMilesSpent());
            dto.setCodigo_voo(booking.getFlightCode());
            dto.setCodigo_aeroporto_origem(booking.getOriginAirport());
            dto.setCodigo_aeroporto_destino(booking.getDestinationAirport());
            
            // Adiciona o status anterior no DTO para compensação
            dto.setStatusAnterior(statusAnterior);

            // Atualiza o status para CANCELADA
            BookingStatus status = bookingStatusRepository.findByCode("CANCELADA");
            if (status == null) {
                throw new RuntimeException("Status CANCELADA não encontrado");
            }
            
            booking.setStatus(status);
            bookingRepository.save(booking);
            publishBookingEvent("CANCELLED", booking);
            
            System.out.println("[RESERVA] Reserva " + codigoReserva + " cancelada com sucesso na SAGA");
            return true;
            
        } catch (Exception e) {
            System.err.println("[RESERVA] Erro ao cancelar reserva na SAGA: " + e.getMessage());
            throw e;
        }
    }

    public void revertCancellationBySaga(ReservationDTO dto) {
        try {
            String codigoReserva = dto.getCodigo_reserva();
            String statusAnterior = dto.getStatusAnterior();
            
            if (statusAnterior == null) {
                statusAnterior = "CRIADA"; // fallback
            }
            
            // Busca a reserva pelo código
            Booking booking = bookingRepository.findById(codigoReserva).orElse(null);
            if (booking == null) {
                System.err.println("[RESERVA] Reserva não encontrada para compensação: " + codigoReserva);
                return;
            }

            // Volta para o status anterior
            BookingStatus status = bookingStatusRepository.findByCode(statusAnterior);
            if (status == null) {
                System.err.println("[RESERVA] Status anterior não encontrado: " + statusAnterior + ". Voltando para CRIADA");
                status = bookingStatusRepository.findByCode("CRIADA");
            }
            
            if (status != null) {
                booking.setStatus(status);
                bookingRepository.save(booking);
                publishBookingEvent("UPDATED", booking);
                
                System.out.println("[RESERVA] Cancelamento da reserva " + codigoReserva + " foi revertido para " + statusAnterior);
            }
            
        } catch (Exception e) {
            System.err.println("[RESERVA] Erro ao reverter cancelamento na SAGA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BookingResponseDTO toResponseDTO(Booking booking, BookingRequestDTO dto) {
        try {
            System.out.println("Iniciando toResponseDTO...");
            BookingResponseDTO response = new BookingResponseDTO();
            System.out.println("BookingResponseDTO criado...");
            
            response.codigo = booking.getCode();
            System.out.println("Código setado: " + response.codigo);

            // Formatar data para ISO 8601 com timezone de São Paulo
            if (booking.getDate() != null) {
                System.out.println("Formatando data...");
                ZonedDateTime zdt;
                
                // Verifica o tipo da data e converte adequadamente
                if (booking.getDate() instanceof java.sql.Date) {
                    // Para java.sql.Date, converte para java.util.Date primeiro
                    java.sql.Date sqlDate = (java.sql.Date) booking.getDate();
                    zdt = Instant.ofEpochMilli(sqlDate.getTime())
                        .atZone(ZoneId.of("America/Sao_Paulo"));
                } else {
                    // Para java.util.Date ou java.sql.Timestamp
                    zdt = booking.getDate().toInstant()
                        .atZone(ZoneId.of("America/Sao_Paulo"));
                }
                
                response.data = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                System.out.println("Data formatada: " + response.data);
            } else {
                response.data = null;
                System.out.println("Data é null");
            }    
            
            System.out.println("Setando outros campos...");
            response.valor = booking.getMoneySpent() != null ? booking.getMoneySpent().doubleValue() : null;
            response.milhas_utilizadas = booking.getMilesSpent();
            response.quantidade_poltronas = booking.getTotalSeats();
            response.codigo_cliente = booking.getClientId(); 
            response.estado = booking.getStatus() != null ? booking.getStatus().getCode() : null;
            response.codigo_voo = booking.getFlightCode();
            response.codigo_aeroporto_origem = booking.getOriginAirport();
            response.codigo_aeroporto_destino = booking.getDestinationAirport();
            
            System.out.println("toResponseDTO concluído com sucesso!");
            return response;
        } catch (Exception e) {
            System.out.println("Erro em toResponseDTO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}