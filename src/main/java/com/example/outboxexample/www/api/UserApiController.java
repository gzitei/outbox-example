package com.example.outboxexample.www.api;

import com.example.outboxexample.model.User;
import com.example.outboxexample.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService service;

    public UserApiController(final UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAll() {
        return this.service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable(name = "id") final Long id) {
        return this.service.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody final User user) {
        return this.service.addUser(user);
    }

    @PatchMapping
    @Transactional
    public User updateUser(@RequestBody final Map<String, Object> payload) {
        return this.service.updateUser(payload);
    }

}
