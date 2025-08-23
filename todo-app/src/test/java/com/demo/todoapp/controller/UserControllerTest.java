package com.demo.todoapp.controller;

import com.demo.todoapp.dto.UserRequest;
import com.demo.todoapp.dto.UserResponse;
import com.demo.todoapp.exception.DuplicateResourceException;
import com.demo.todoapp.exception.ResourceNotFoundException;
import com.demo.todoapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.demo.todoapp.controller.ApiConstant.BASE_PATH;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private final String BASE_URL = BASE_PATH + "/users";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser() throws Exception {
        UserRequest userRequest = createUserRequest();
        UserResponse userResponse = createUserResponse();

        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("alex.martinez")))
                .andExpect(jsonPath("$.email", is("alex.martinez@gmail.com")));
    }

    @Test
    void testCreateUser_WithDuplicateUsername_ThrowsException() throws Exception {
        UserRequest userRequest = createUserRequest();

        when(userService.createUser(any(UserRequest.class))).thenThrow(new DuplicateResourceException("Username already exists: alex.martinez"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateUser_WithDuplicateEmail_ThrowsException() throws Exception {
        UserRequest userRequest = createUserRequest();

        when(userService.createUser(any(UserRequest.class))).thenThrow(new DuplicateResourceException("Email already exists: alex.martinez@gmail.com"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateUser_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        UserRequest invalidRequest = new UserRequest();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetUserById() throws Exception {
        UserResponse userResponse = createUserResponse();

        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("alex.martinez")))
                .andExpect(jsonPath("$.email", is("alex.martinez@gmail.com")));
    }

    @Test
    void testGetUserById_WithNonExistentId_ThrowsException() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(get(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserByUsername() throws Exception {
        UserResponse userResponse = createUserResponse();

        when(userService.getUserByUsername("alex.martinez")).thenReturn(userResponse);

        mockMvc.perform(get(BASE_URL + "/username/alex.martinez")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("alex.martinez")))
                .andExpect(jsonPath("$.email", is("alex.martinez@gmail.com")));
    }

    @Test
    void testGetUserByUsername_WithNonExistentUsername_ThrowsException() throws Exception {
        when(userService.getUserByUsername("nonexistent.user")).thenThrow(new ResourceNotFoundException("User not found with username: nonexistent.user"));

        mockMvc.perform(get(BASE_URL + "/username/nonexistent.user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserByEmail() throws Exception {
        UserResponse userResponse = createUserResponse();

        when(userService.getUserByEmail("alex.martinez@gmail.com")).thenReturn(userResponse);

        mockMvc.perform(get(BASE_URL + "/email/alex.martinez@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("alex.martinez")))
                .andExpect(jsonPath("$.email", is("alex.martinez@gmail.com")));
    }

    @Test
    void testGetUserByEmail_WithNonExistentEmail_ThrowsException() throws Exception {
        when(userService.getUserByEmail("nonexistent@example.com")).thenThrow(new ResourceNotFoundException("User not found with email: nonexistent@example.com"));

        mockMvc.perform(get(BASE_URL + "/email/nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private UserRequest createUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("alex.martinez");
        userRequest.setEmail("alex.martinez@gmail.com");
        userRequest.setPassword("SecurePass123!");

        return userRequest;
    }

    private UserResponse createUserResponse() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("alex.martinez");
        userResponse.setEmail("alex.martinez@gmail.com");

        return userResponse;
    }
}
