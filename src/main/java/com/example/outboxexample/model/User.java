package com.example.outboxexample.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record User(Long id, String email, String name, String password, LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
