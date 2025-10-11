package com.example.outboxexample.data.entity;

import com.example.outboxexample.data.enums.MessageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "outbox_messages")
@NoArgsConstructor
@AllArgsConstructor
public class OutboxMessageEntity {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_message_id")
    private Long outboxMessageId;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "content")
    private String content;

    @Column(name = "sent_at", nullable = true)
    private LocalDateTime sentAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
