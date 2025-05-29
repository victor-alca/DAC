package com.orchestrator.orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlightSagaConfig {

    // Queue, exchange e routing key que o serviço de voos escuta (Orchestrator publica)
    public static final String FLIGHT_QUEUE = "flight.saga.queue";
    public static final String FLIGHT_EXCHANGE = "flight.saga.exchange";
    public static final String FLIGHT_ROUTING_KEY = "flight.saga.routingKey";

    @Bean
    public Queue flightQueue() {
        return new Queue(FLIGHT_QUEUE, true);
    }

    @Bean
    public DirectExchange flightExchange() {
        return new DirectExchange(FLIGHT_EXCHANGE);
    }

    @Bean
    public Binding flightBinding() {
        return BindingBuilder
            .bind(flightQueue())
            .to(flightExchange())
            .with(FLIGHT_ROUTING_KEY);
    }
}