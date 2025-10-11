package com.example.outboxexample.worker;

import com.example.outboxexample.model.OutboxMessage;
import com.example.outboxexample.service.OutboxMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxMessageProducer {

    private final String exchangeName;
    private final String routeName;
    private final RabbitTemplate rabbitTemplate;
    private final OutboxMessageService service;
    private final ObjectMapper mapper;

    public OutboxMessageProducer(@Value("${worker.exchange.name}") final String exchangeName,
            @Value("${worker.route.name}") final String routeName,
            final RabbitTemplate rabbitTemplate, final OutboxMessageService service,
            final ObjectMapper mapper) {
        this.exchangeName = exchangeName;
        this.routeName = routeName;
        this.rabbitTemplate = rabbitTemplate;
        this.service = service;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void sendMessage() {
        try {
            final OutboxMessage message = this.service.getNextMessage();
            if (message == null) {
                return;
            }
            final String payload = this.mapper.writeValueAsString(message);
            this.rabbitTemplate.convertAndSend(this.exchangeName, this.routeName, payload);
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
