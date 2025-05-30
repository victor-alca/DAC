package com.orchestrator.orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationSagaConfig {

    // Queue, exchange e routing key que o serviço de reservas escuta (Orchestrator publica)
    public static final String RESERVATION_QUEUE = "reservation.saga.queue";
    public static final String RESERVATION_EXCHANGE = "reservation.saga.exchange";
    public static final String RESERVATION_ROUTING_KEY = "reservation.saga.routingKey";

    // Queue, exchange e routing key que o Orchestrator escuta (Serviço de reservas publica)
    public static final String ORCHESTRATOR_QUEUE = "orchestrator.saga.queue";
    public static final String ORCHESTRATOR_EXCHANGE = "orchestrator.saga.exchange";
    public static final String ORCHESTRATOR_ROUTING_KEY = "orchestrator.saga.routingKey";

    // Fila que o serviço de reservas escuta (Orchestrator publica)
    @Bean
    public Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, true);
    }

    @Bean
    public DirectExchange reservationExchange() {
        return new DirectExchange(RESERVATION_EXCHANGE);
    }

    @Bean
    public Binding reservationBinding() {
        return BindingBuilder
            .bind(reservationQueue())
            .to(reservationExchange())
            .with(RESERVATION_ROUTING_KEY);
    }

    // Fila que o Orchestrator escuta (Serviço de reservas publica)
    @Bean
    public Queue orchestratorQueue() {
        return new Queue(ORCHESTRATOR_QUEUE, true);
    }

    @Bean
    public DirectExchange orchestratorExchange() {
        return new DirectExchange(ORCHESTRATOR_EXCHANGE);
    }

    @Bean
    public Binding orchestratorBinding() {
        return BindingBuilder
            .bind(orchestratorQueue())
            .to(orchestratorExchange())
            .with(ORCHESTRATOR_ROUTING_KEY);
    }
}
