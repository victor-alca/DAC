package backend.clients.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import backend.clients.message.SagaMessage;
import backend.clients.services.ClientService;
import backend.clients.dto.MilesBalanceDTO;
import backend.clients.dto.ReservationDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MilhasSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ClientService clientService;

    private boolean debitarMilhas(ReservationDTO dto) {
        try {
            int codigoCliente = dto.getCodigo_cliente();
            Double milhasParaDebitar = dto.getMilhas_utilizadas() != null ? dto.getMilhas_utilizadas().doubleValue() : 0.0;
            
            // 1. Debita as milhas do saldo do cliente
            MilesBalanceDTO result = clientService.debitMiles(codigoCliente, milhasParaDebitar);
            
            // 2. Registra a operação de débito no extrato
            clientService.recordMilesDebit(
                codigoCliente,
                milhasParaDebitar,
                "Débito para reserva", // descrição será atualizada depois
                "" // bookingCode será preenchido depois
            );
            
            return result != null; // Se retornou o DTO, a operação foi bem-sucedida
        } catch (ResponseStatusException e) {
            // Log do erro específico
            System.err.println("[MILHAS] Erro ao debitar milhas: " + e.getReason());
            throw e; // Re-lança para ser capturada no listener
        }
    }

    @RabbitListener(queues = "reserva.criacao.iniciada.milhas")
    public void onMilhasSaga(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            
            // Tenta debitar milhas - pode lançar ResponseStatusException
            boolean ok = debitarMilhas(message.getPayload());
            message.setOrigin("MILHAS");

            if (ok) {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.sucesso", objectMapper.writeValueAsString(message));
            } else {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            }
        } catch (ResponseStatusException e) {
            // Captura exceções específicas (CONFLICT, NOT_FOUND, etc.)
            System.err.println("[MILHAS] Falha na SAGA - " + e.getStatusCode() + ": " + e.getReason());
            
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<ReservationDTO>>() {}
                );
                message.setOrigin("MILHAS");
                
                // Adiciona informações do erro na mensagem
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("errorCode", e.getStatusCode().value());
                errorInfo.put("errorMessage", e.getReason());
                // Se você quiser passar o erro específico, pode adicionar no payload ou criar um campo específico
                
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            // Outros erros inesperados
            e.printStackTrace();
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<ReservationDTO>>() {}
                );
                message.setOrigin("MILHAS");
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = "reserva.milhas.compensar")
    public void onCompensate(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            ReservationDTO dto = message.getPayload();

            // 1. Rollback: devolve as milhas debitadas
            clientService.addMiles(dto.getCodigo_cliente(), dto.getMilhas_utilizadas().doubleValue());
            
            // 2. Registra a operação de rollback no extrato
            clientService.recordMilesTransaction(
                dto.getCodigo_cliente(),
                dto.getMilhas_utilizadas().doubleValue(),
                "ENTRADA",
                "Rollback - Falha na reserva",
                dto.getCodigo_reserva() != null ? dto.getCodigo_reserva() : ""
            );
            
            System.out.println("[CLIENTES] Rollback de milhas realizado para cliente " + dto.getCodigo_cliente());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "reserva.criacao.iniciada.atualizar.milhas")
    public void onAtualizarMilhasSaga(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            
            ReservationDTO dto = message.getPayload();
            
            // Atualiza o registro de milhas com informações da reserva
            clientService.updateMilesRecordWithFlightInfo(
                dto.getCodigo_cliente(),
                dto.getCodigo_aeroporto_origem(),
                dto.getCodigo_aeroporto_destino(),
                dto.getCodigo_reserva()
            );
            
            message.setOrigin("ATUALIZAR_MILHAS");
            rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.sucesso", objectMapper.writeValueAsString(message));
            
            System.out.println("[ATUALIZAR_MILHAS] Registro de milhas atualizado para cliente " + 
                dto.getCodigo_cliente() + " com reserva " + dto.getCodigo_reserva());
                
        } catch (Exception e) {
            System.err.println("[ATUALIZAR_MILHAS] Erro ao atualizar registro de milhas: " + e.getMessage());
            
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<ReservationDTO>>() {}
                );
                message.setOrigin("ATUALIZAR_MILHAS");
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
