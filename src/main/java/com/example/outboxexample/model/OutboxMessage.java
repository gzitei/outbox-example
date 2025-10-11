package com.example.outboxexample.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OutboxMessage(Long id, Long recipientId, String content, LocalDateTime sentAt,
        String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
