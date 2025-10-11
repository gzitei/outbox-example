package com.example.outboxexample.www.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.outboxexample.data.entity.OutboxMessageEntity;
import com.example.outboxexample.data.entity.UserEntity;
import com.example.outboxexample.data.repository.OutboxMessageRepository;
import com.example.outboxexample.data.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rollback
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OutboxMessageRepository outboxMessageRepository;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void createUser() {
        final UserEntity entity = new UserEntity();
        entity.setName("Test User");
        entity.setEmail("user.test@test.com");
        entity.setPassword("ushalln0tp@ss");
        this.userRepository.save(entity);
    }

    @Test
    void shouldCreateOutboxMessageWhenPasswordIsUpdated() throws Exception {
        // given
        final UserEntity existingEntity = this.userRepository.findAll().get(0);
        final Long userId = existingEntity.getUserId();
        final String newPassword = "secretp@ssw0rd";
        final Map<String, Object> payload = new HashMap<>();
        payload.put("id", userId);
        payload.put("password", newPassword);
        final String endpoint = "/api/users";

        // when
        this.mockMvc
                .perform(patch(endpoint).contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        // then
        final List<UserEntity> userEntities = this.userRepository.findAll();
        assertEquals(1, userEntities.size());
        final List<OutboxMessageEntity> outboxMessagesEntities =
                this.outboxMessageRepository.findAll();
        assertEquals(1, outboxMessagesEntities.size());
        final UserEntity updatedEntity = userEntities.get(0);
        final OutboxMessageEntity outboxMessageEntity = outboxMessagesEntities.get(0);
        assertEquals(existingEntity.getUserId(), updatedEntity.getUserId());
        assertEquals(existingEntity.getEmail(), updatedEntity.getEmail());
        assertEquals(existingEntity.getName(), updatedEntity.getName());
        assertEquals(newPassword, updatedEntity.getPassword());
        assertEquals(userId, outboxMessageEntity.getRecipientId());
    }

}
