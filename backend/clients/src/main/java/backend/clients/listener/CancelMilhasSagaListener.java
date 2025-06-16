package backend.clients.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import backend.clients.dto.ReservationDTO;
import backend.clients.message.SagaMessage;
import backend.clients.services.ClientService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CancelMilhasSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ClientService clientService;

    @RabbitListener(queues = "reserva.cancelamento.iniciado.milhas")
    public void onDevolverMilhasSaga(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            
            ReservationDTO dto = message.getPayload();
            
            System.out.println("[DEVOLVER_MILHAS] Processando devolução de milhas para cliente " + 
                dto.getCodigo_cliente() + " - Reserva: " + dto.getCodigo_reserva());
            
            // Devolve as milhas e registra no extrato
            clientService.returnMilesFromCancellation(
                dto.getCodigo_cliente(),
                dto.getMilhas_utilizadas().doubleValue(),
                dto.getCodigo_reserva()
            );
            
            message.setOrigin("DEVOLVER_MILHAS");
            rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.sucesso", objectMapper.writeValueAsString(message));
            
            System.out.println("[DEVOLVER_MILHAS] Milhas devolvidas com sucesso para cliente " + 
                dto.getCodigo_cliente() + " por cancelamento da reserva " + dto.getCodigo_reserva());
                
        } catch (ResponseStatusException e) {
            System.err.println("[DEVOLVER_MILHAS] Falha na SAGA - " + e.getStatusCode() + ": " + e.getReason());
            
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<ReservationDTO>>() {}
                );
                message.setOrigin("DEVOLVER_MILHAS");
                
                // Adiciona informações do erro na mensagem
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("errorCode", e.getStatusCode().value());
                errorInfo.put("errorMessage", e.getReason());
                message.setErrorInfo(errorInfo);
                                
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("[DEVOLVER_MILHAS] Erro inesperado ao devolver milhas: " + e.getMessage());
            e.printStackTrace();
            
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<ReservationDTO>>() {}
                );
                message.setOrigin("DEVOLVER_MILHAS");
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = "reserva.devolver.milhas.compensar")
    public void onCompensateReturnMilhas(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            
            ReservationDTO dto = message.getPayload();
            String correlationId = message.getCorrelationId();

            System.out.println("[CLIENTES] Executando compensação de devolução de milhas para SAGA " + correlationId);
            System.out.println("[CLIENTES] Debitando de volta " + dto.getMilhas_utilizadas() + " milhas do cliente " + dto.getCodigo_cliente());

            // Reverte a devolução de milhas - debita novamente as milhas que foram devolvidas
            clientService.debitMiles(dto.getCodigo_cliente(), dto.getMilhas_utilizadas().doubleValue());
            
            // Registra a operação de compensação no extrato
            clientService.recordMilesTransaction(
                dto.getCodigo_cliente(),
                -dto.getMilhas_utilizadas().doubleValue(), // negativo para indicar débito
                "SAIDA",
                "Compensação - Falha no cancelamento de reserva",
                dto.getCodigo_reserva() != null ? dto.getCodigo_reserva() : ""
            );

            System.out.println("[CLIENTES] Compensação de devolução de milhas concluída para cliente " + dto.getCodigo_cliente());
            
        } catch (Exception e) {
            System.err.println("[CLIENTES] Erro na compensação de devolução de milhas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}