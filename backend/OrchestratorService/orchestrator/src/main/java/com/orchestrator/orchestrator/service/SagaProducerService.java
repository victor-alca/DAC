package com.orchestrator.orchestrator.service;

import com.orchestrator.orchestrator.rabbitmq.dto.InitiateBookingSagaComandoDTO;

public interface SagaProducerService {
    void sendStartBookingSagaCommand(InitiateBookingSagaComandoDTO comando);
}