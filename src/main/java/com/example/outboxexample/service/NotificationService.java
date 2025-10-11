package com.example.outboxexample.service;

import com.example.outboxexample.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final UserService userService;

    public NotificationService(final UserService userService) {
        this.userService = userService;
    }

    public void notifyUserById(final Long id, final String message) {
        final User user = this.userService.getUserById(id);
        log.info("Message '{}' sent for user id {} in email {}", message, id, user.email());
    }
}
