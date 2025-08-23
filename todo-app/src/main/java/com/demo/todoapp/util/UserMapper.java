package com.demo.todoapp.util;

import com.demo.todoapp.dto.UserRequest;
import com.demo.todoapp.dto.UserResponse;
import com.demo.todoapp.entity.User;

public class UserMapper {
    public static User toEntity(UserRequest dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());

        return user;
    }

    public static UserResponse toResponse(User entity) {
        UserResponse response = new UserResponse();

        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setEmail(entity.getEmail());

        return response;
    }
}
