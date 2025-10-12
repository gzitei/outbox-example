package com.example.outboxexample.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.example.outboxexample.data.entity.OutboxMessageEntity;
import com.example.outboxexample.data.entity.UserEntity;
import com.example.outboxexample.data.repository.OutboxMessageRepository;
import com.example.outboxexample.data.repository.UserRepository;
import com.example.outboxexample.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rollback
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    OutboxMessageRepository outboxMessageRepository;

    @BeforeEach
    void createUser() {
        final User created = User.builder().name("Test User").email("user.test@test.com")
                .password("ushalln0tp@ss").build();
        this.userService.addUser(created);
    }

    @Test
    void shouldRollbackUserUpdateIfOutboxMessageRepositoryFails() throws RuntimeException {
        // given
        final User existing = this.userService.getAllUsers().get(0);
        final String oldPassword = existing.password();
        final String newPassword = "sup3rsecr3t";
        final Map<String, Object> payload = new HashMap<>();
        payload.put("id", existing.id());
        payload.put("password", newPassword);

        // when
        doThrow(new RuntimeException("Database Error!")).when(this.outboxMessageRepository)
                .save(any(OutboxMessageEntity.class));
        assertThrows(RuntimeException.class, () -> this.userService.updateUser(payload));

        final List<UserEntity> users = this.userRepository.findAll();

        // then
        assertEquals(1, users.size());
        final UserEntity user = users.get(0);
        final List<OutboxMessageEntity> messages = outboxMessageRepository.findAll();
        assertEquals(0, messages.size());
        assertEquals(existing.id(), user.getUserId());
        assertEquals(existing.name(), user.getName());
        assertEquals(existing.email(), user.getEmail());
        assertFalse(newPassword.equals(user.getPassword()));
        assertTrue(oldPassword.equals(user.getPassword()));
    }

}


