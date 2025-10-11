package com.example.outboxexample.data.repository;

import com.example.outboxexample.data.entity.OutboxMessageEntity;
import com.example.outboxexample.data.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, Long> {
    Optional<OutboxMessageEntity> findFirstBySentAtNullAndStatus(
            @Param("status") MessageStatus status);
}
