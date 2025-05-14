package com.orchestrator.orchestrator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.orchestrator.controller.dto.CreateBookingHttpRequestDTO;
import com.orchestrator.orchestrator.rabbitmq.dto.InitiateBookingSagaComandoDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reservas") // Endpoint basa das sagas de reserva
public class SagaController {

    private static final Logger logger = LoggerFactory.getLogger(SagaController.class);

    @Autowired
    private com.orchestrator.orchestrator.service.SagaProducerService sagaProducerService;

    // O API Gateway chamará esse endpoint para iniciar a saga de reserva
    @PostMapping("/reservas/iniciar")
    public ResponseEntity<Map<String, String>> startBookingSaga(@RequestBody CreateBookingHttpRequestDTO requestHttpDTO) {
        String idSaga = UUID.randomUUID().toString();
        try {
            logger.info("Serviço de Sagas: Recebida requisição HTTP para iniciar saga de reserva. ID da Saga gerado: {}", idSaga);
            logger.info("Payload da requisição HTTP: {}", requestHttpDTO);

            // Mapear do DTO da requisição HTTP para o DTO do comando RabbitMQ
            InitiateBookingSagaComandoDTO comandoRabbitDTO = new InitiateBookingSagaComandoDTO(
                    idSaga,
                    requestHttpDTO.getCodigoCliente(),
                    requestHttpDTO.getValor(),
                    requestHttpDTO.getMilhasUtilizadas(),
                    requestHttpDTO.getQuantidadePoltronas(),
                    requestHttpDTO.getCodigoVoo()
                    // Os códigos de aeroporto não foram incluídos no comando DTO por simplicidade,
                    // mas poderiam ser se o primeiro passo da saga precisasse deles.
            );

            sagaProducerService.sendStartBookingSagaCommand(comandoRabbitDTO);

            Map<String, String> response = new HashMap<>();
            response.put("id_saga", idSaga);
            response.put("status", "INICIADA");
            response.put("mensagem", "Processo de criação de reserva iniciado.");

            // Resposta imediata para o API Gateway
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            logger.error("Serviço de Sagas: Erro ao iniciar saga de reserva com id_saga {}: ", idSaga, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("id_saga", idSaga);
            errorResponse.put("status", "FALHA_AO_INICIAR");
            errorResponse.put("mensagem", "Erro interno ao tentar iniciar o processo de reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
