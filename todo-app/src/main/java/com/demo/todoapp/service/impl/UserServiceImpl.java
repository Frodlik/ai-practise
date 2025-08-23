package com.demo.todoapp.service.impl;

import com.demo.todoapp.dto.UserRequest;
import com.demo.todoapp.dto.UserResponse;
import com.demo.todoapp.entity.User;
import com.demo.todoapp.exception.DuplicateResourceException;
import com.demo.todoapp.exception.ResourceNotFoundException;
import com.demo.todoapp.repository.UserRepository;
import com.demo.todoapp.service.UserService;
import com.demo.todoapp.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists: " + userRequest.getUsername());
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + userRequest.getEmail());
        }

        User user = UserMapper.toEntity(userRequest);

        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return UserMapper.toResponse(user);
    }
}
