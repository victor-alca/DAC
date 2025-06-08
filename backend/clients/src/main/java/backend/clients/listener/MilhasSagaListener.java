package backend.clients.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import backend.clients.message.SagaMessage;
import backend.clients.services.ClientService;
import backend.clients.dto.ReservationDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MilhasSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ClientService clientService;

    private boolean debitarMilhas(ReservationDTO dto) {
        int codigoCliente = dto.getCodigo_cliente();
        Double milhasParaDebitar = dto.getMilhas_utilizadas() != null ? dto.getMilhas_utilizadas().doubleValue() : 0.0;
        return clientService.debitarMilhas(codigoCliente, milhasParaDebitar);
    }

    @RabbitListener(queues = "reserva.criacao.iniciada.milhas")
    public void onMilhasSaga(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            // 1. Processa a lógica de débito de milhas
            boolean ok = debitarMilhas(message.getPayload());
            message.setOrigin("MILHAS");

            if (ok) {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.sucesso", objectMapper.writeValueAsString(message));
            } else {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de erro inesperado, envie falha
            try {
                SagaMessage message = objectMapper.readValue(json, SagaMessage.class);
                message.setOrigin("MILHAS");
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
