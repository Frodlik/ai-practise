package com.demo.todoapp.service;

import com.demo.todoapp.dto.UserRequest;
import com.demo.todoapp.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse getUserByEmail(String email);
}
