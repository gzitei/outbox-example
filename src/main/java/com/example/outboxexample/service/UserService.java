package com.example.outboxexample.service;

import com.example.outboxexample.data.entity.UserEntity;
import com.example.outboxexample.data.repository.UserRepository;
import com.example.outboxexample.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class UserService {

    private final UserRepository repository;
    private final OutboxMessageService outboxMessageService;

    public UserService(final UserRepository repository,
            final OutboxMessageService outboxMessageService) {
        this.repository = repository;
        this.outboxMessageService = outboxMessageService;
    }

    @Transactional
    public List<User> getAllUsers() {
        return this.repository.findAll().stream().map(this::getUserFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public User getUserById(final Long id) {
        final UserEntity entity = this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by id: " + id));
        return this.getUserFromEntity(entity);
    }

    @Transactional
    public User addUser(final User user) {
        final UserEntity entity = this.repository.save(this.getEntityFromUser(user));
        return this.getUserFromEntity(entity);
    }

    @Transactional
    public User updateUser(final Map<String, Object> payload) {
        if (!payload.containsKey("id")) {
            throw new IllegalArgumentException("Missing field 'id' in request body.");
        }
        final Long userId = Long.valueOf(payload.get("id").toString());
        final UserEntity existingEntity = this.repository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Could not update user by id: " + userId));

        final Boolean shouldNotifyUser = payload.containsKey("password");
        payload.forEach((key, value) -> {
            switch (key) {
                case "name":
                    existingEntity.setName(value.toString());
                    break;
                case "email":
                    existingEntity.setEmail(value.toString());
                    break;
                case "password":
                    existingEntity.setPassword(value.toString());
                    break;
                default:
                    break;
            }
        });

        final UserEntity entity = this.repository.save(existingEntity);
        if (shouldNotifyUser) {
            final String content = "Your password has been updated at "
                    + entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.outboxMessageService.newOutboxMessage(content, userId);
        }
        return this.getUserFromEntity(entity);
    }

    @Transactional
    public void deleteUser(final User user) {
        this.repository.deleteById(user.id());
    }

    private User getUserFromEntity(final UserEntity entity) {
        return new User(entity.getUserId(), entity.getEmail(), entity.getName(),
                entity.getPassword(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private UserEntity getEntityFromUser(final User user) {
        return new UserEntity(user.id(), user.email(), user.name(), user.password(),
                user.createdAt(), user.updatedAt());
    }

}
