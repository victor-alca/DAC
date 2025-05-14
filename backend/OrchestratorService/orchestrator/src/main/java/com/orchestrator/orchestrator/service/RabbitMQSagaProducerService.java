package com.orchestrator.orchestrator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.orchestrator.orchestrator.rabbitmq.dto.InitiateBookingSagaComandoDTO;

@Service
public class RabbitMQSagaProducerService implements SagaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQSagaProducerService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange-name}")
    private String sagaExchangeName;

    @Value("${app.rabbitmq.routingkey.iniciar-reserva}")
    private String iniciarReservaRoutingKey; // Routing key para iniciar a saga de reserva

    @Override
    public void sendStartBookingSagaCommand(InitiateBookingSagaComandoDTO comando) {
        logger.info("Enviando comando para iniciar saga de reserva: {}", comando);
        rabbitTemplate.convertAndSend(sagaExchangeName, iniciarReservaRoutingKey, comando);
        logger.info("Comando da saga de reserva enviado para exchange '{}' com routing key '{}'", sagaExchangeName, iniciarReservaRoutingKey);
    }
}