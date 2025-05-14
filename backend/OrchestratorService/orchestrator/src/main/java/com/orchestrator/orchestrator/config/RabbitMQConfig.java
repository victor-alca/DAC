package com.orchestrator.orchestrator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange-name}")
    private String sagaExchangeName;

    // Nome da fila que o serviço de Reservas (ou o primeiro da cadeia) irá escutar
    public static final String RESERVATION_START_COMMAND_QUEUE = "reserva.iniciar.comando.queue";
    @Value("${app.rabbitmq.routingkey.iniciar-reserva}")
    private String iniciarReservaRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(sagaExchangeName);
    }

    @Bean
    public Queue iniciarReservaComandoQueue() {
        return new Queue(RESERVATION_START_COMMAND_QUEUE, true);
    }

    @Bean
    public Binding bindingIniciarReserva(TopicExchange sagaExchange, Queue iniciarReservaComandoQueue) {
        return BindingBuilder.bind(iniciarReservaComandoQueue)
                             .to(sagaExchange)
                             .with(iniciarReservaRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}