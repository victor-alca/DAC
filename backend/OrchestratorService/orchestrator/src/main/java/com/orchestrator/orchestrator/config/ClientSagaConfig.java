package com.orchestrator.orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientSagaConfig {

    // Queue, exchange e routing key que o serviço de clientes escuta (Orchestrator publica)
    public static final String CLIENT_QUEUE = "client.saga.queue";
    public static final String CLIENT_EXCHANGE = "client.saga.exchange";
    public static final String CLIENT_ROUTING_KEY = "client.saga.routingKey";

    @Bean
    public Queue clientQueue() {
        return new Queue(CLIENT_QUEUE, true);
    }

    @Bean
    public DirectExchange clientExchange() {
        return new DirectExchange(CLIENT_EXCHANGE);
    }

    @Bean
    public Binding clientBinding() {
        return BindingBuilder
            .bind(clientQueue())
            .to(clientExchange())
            .with(CLIENT_ROUTING_KEY);
    }
}