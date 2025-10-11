package com.example.outboxexample.worker;

import com.example.outboxexample.model.OutboxMessage;
import com.example.outboxexample.service.NotificationService;
import com.example.outboxexample.service.OutboxMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OutboxMessageConsumer {
    private final ObjectMapper mapper;
    private final OutboxMessageService outboxMessageService;
    private final NotificationService notificationService;

    public OutboxMessageConsumer(final OutboxMessageService outboxMessageService,
            final ObjectMapper mapper, final NotificationService notificationService) {
        this.outboxMessageService = outboxMessageService;
        this.mapper = mapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public void handleMessage(final String message) {
        try {
            final OutboxMessage payload = this.mapper.readValue(message, OutboxMessage.class);
            this.outboxMessageService.markProcessing(payload.id());
            this.notificationService.notifyUserById(payload.recipientId(), payload.content());
            this.outboxMessageService.markSent(payload.id());
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
