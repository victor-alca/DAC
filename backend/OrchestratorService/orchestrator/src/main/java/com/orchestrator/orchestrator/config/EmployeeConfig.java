package com.orchestrator.orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeConfig {
    
    
    // Queue, exchange e routing key que o serviço de employees escuta (Orchestrator publica)
    public static final String EMPLOYEE_QUEUE = "employee.saga.queue";
    public static final String EMPLOYEE_EXCHANGE = "employee.saga.exchange";
    public static final String EMPLOYEE_ROUTING_KEY = "employee.saga.routingKey";

    @Bean
    public Queue employeeQueue() {
        return new Queue(EMPLOYEE_QUEUE, true);
    }

    @Bean
    public DirectExchange employeeExchange() {
        return new DirectExchange(EMPLOYEE_EXCHANGE);
    }

    @Bean
    public Binding employeeBinding() {
        return BindingBuilder
            .bind(employeeQueue())
            .to(employeeExchange())
            .with(EMPLOYEE_ROUTING_KEY);
    }
}
