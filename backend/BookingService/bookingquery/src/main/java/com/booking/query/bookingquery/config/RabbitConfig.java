package com.booking.query.bookingquery.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitConfig {
    public static final String BOOKING_EXCHANGE = "booking.events";
    public static final String BOOKING_QUERY_QUEUE = "booking.query";

    @Bean
    public Queue bookingQueryQueue() {
        return new Queue(BOOKING_QUERY_QUEUE);
    }

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue bookingQueryQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingQueryQueue).to(bookingExchange).with("booking.*");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}