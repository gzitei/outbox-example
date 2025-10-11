package com.example.outboxexample.service;

import com.example.outboxexample.data.entity.OutboxMessageEntity;
import com.example.outboxexample.data.enums.MessageStatus;
import com.example.outboxexample.data.repository.OutboxMessageRepository;
import com.example.outboxexample.model.OutboxMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
public class OutboxMessageService {

    private final OutboxMessageRepository repository;

    public OutboxMessageService(final OutboxMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<OutboxMessage> getAllOutboxMessages() {
        return this.repository.findAll().stream().map(this::getOutboxMessageFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OutboxMessage addOutboxMessage(final OutboxMessage message) {
        final OutboxMessageEntity entity =
                this.repository.save(this.getEntityFromOutboxMessage(message));
        return this.getOutboxMessageFromEntity(entity);
    }

    @Transactional
    public OutboxMessage getById(final Long id) {
        final OutboxMessageEntity entity = this.repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Could not find message by id: " + id));
        return this.getOutboxMessageFromEntity(entity);
    }

    @Transactional
    public OutboxMessage updateOutboxMessage(final OutboxMessage message) {
        if (!this.repository.existsById(message.id())) {
            throw new EntityNotFoundException("Could not update message by id: " + message.id());
        }
        final OutboxMessageEntity entity =
                this.repository.save(this.getEntityFromOutboxMessage(message));
        return this.getOutboxMessageFromEntity(entity);
    }

    @Transactional
    public OutboxMessage newOutboxMessage(final String content, final Long recipientId) {
        final OutboxMessage message = OutboxMessage.builder().status(MessageStatus.PENDING.name())
                .recipientId(recipientId).content(content).build();
        return this.addOutboxMessage(message);
    }

    @Transactional
    public OutboxMessage getNextMessage() {
        final Optional<OutboxMessageEntity> entityOptional =
                this.repository.findFirstBySentAtNullAndStatus(MessageStatus.PENDING);
        if (entityOptional.isEmpty()) {
            return null;
        }
        final OutboxMessageEntity entity = entityOptional.get();
        return this.getOutboxMessageFromEntity(entity);
    }

    @Transactional
    public OutboxMessage markSent(final Long id) {
        final OutboxMessage message = this.getById(id);
        final OutboxMessageEntity entity = this.getEntityFromOutboxMessage(message);
        entity.setStatus(MessageStatus.SENT);
        entity.setSentAt(LocalDateTime.now());
        return this.getOutboxMessageFromEntity(this.repository.save(entity));
    }

    @Transactional
    public OutboxMessage markProcessing(final Long id) {
        final OutboxMessage message = this.getById(id);
        final OutboxMessageEntity entity = this.getEntityFromOutboxMessage(message);
        entity.setStatus(MessageStatus.PROCESSING);
        return this.getOutboxMessageFromEntity(this.repository.save(entity));
    }

    private OutboxMessage getOutboxMessageFromEntity(final OutboxMessageEntity entity) {
        return new OutboxMessage(entity.getOutboxMessageId(), entity.getRecipientId(),
                entity.getContent(), entity.getSentAt(), entity.getStatus().name(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private OutboxMessageEntity getEntityFromOutboxMessage(final OutboxMessage message) {
        return new OutboxMessageEntity(message.id(), message.recipientId(), message.content(),
                message.sentAt(), MessageStatus.valueOf(message.status()), message.createdAt(),
                message.updatedAt());
    }

}
