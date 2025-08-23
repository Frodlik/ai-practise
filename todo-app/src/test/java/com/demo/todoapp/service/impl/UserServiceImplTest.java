package com.demo.todoapp.service.impl;

import com.demo.todoapp.dto.UserRequest;
import com.demo.todoapp.dto.UserResponse;
import com.demo.todoapp.entity.User;
import com.demo.todoapp.exception.DuplicateResourceException;
import com.demo.todoapp.exception.ResourceNotFoundException;
import com.demo.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser() {
        UserRequest userRequest = createUserRequest();
        User savedUser = createUser();

        when(userRepository.findByUsername("alex.martinez")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alex.martinez@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse actual = userService.createUser(userRequest);

        assertNotNull(actual);
        assertEquals("alex.martinez", actual.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_ThrowsDuplicateUsernameException() {
        UserRequest userRequest = createUserRequest();
        User existingUser = createUser();

        when(userRepository.findByUsername("alex.martinez")).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void testCreateUser_ThrowsDuplicateEmailException() {
        UserRequest userRequest = createUserRequest();
        User existingUser = createUser();

        when(userRepository.findByUsername("alex.martinez")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alex.martinez@gmail.com")).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void testGetUserById() {
        User user = createUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse actual = userService.getUserById(1L);

        assertEquals("alex.martinez", actual.getUsername());
        assertEquals("alex.martinez@gmail.com", actual.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testGetUserByUsername() {
        User user = createUser();

        when(userRepository.findByUsername("alex.martinez")).thenReturn(Optional.of(user));

        UserResponse actual = userService.getUserByUsername("alex.martinez");

        assertEquals("alex.martinez", actual.getUsername());
        assertEquals("alex.martinez@gmail.com", actual.getEmail());
        verify(userRepository, times(1)).findByUsername("alex.martinez");
    }

    @Test
    void testGetUserByUsername_ThrowsException() {
        when(userRepository.findByUsername("alex.martinez")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("alex.martinez"));
    }

    @Test
    void testGetUserByEmail() {
        User user = createUser();

        when(userRepository.findByEmail("alex.martinez@gmail.com")).thenReturn(Optional.of(user));

        UserResponse actual = userService.getUserByEmail("alex.martinez@gmail.com");

        assertEquals("alex.martinez", actual.getUsername());
        assertEquals("alex.martinez@gmail.com", actual.getEmail());
        verify(userRepository, times(1)).findByEmail("alex.martinez@gmail.com");
    }

    @Test
    void testGetUserByEmail_ThrowsException() {
        when(userRepository.findByEmail("alex.martinez@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("alex.martinez@gmail.com"));
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alex.martinez");
        user.setEmail("alex.martinez@gmail.com");
        user.setPassword("SecurePass123!");

        return user;
    }

    private UserRequest createUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("alex.martinez");
        userRequest.setEmail("alex.martinez@gmail.com");
        userRequest.setPassword("SecurePass123!");

        return userRequest;
    }
}