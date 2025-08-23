package com.demo.todoapp.util;

import com.demo.todoapp.dto.TodoRequest;
import com.demo.todoapp.dto.TodoResponse;
import com.demo.todoapp.entity.Todo;

public class TodoMapper {
    public static Todo toEntity(TodoRequest dto) {
        Todo todo = new Todo();

        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.getCompleted());
        todo.setDueDate(dto.getDueDate());

        return todo;
    }

    public static Todo updateEntity(Todo todo, TodoRequest dto) {
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.getCompleted());
        todo.setDueDate(dto.getDueDate());

        return todo;
    }

    public static TodoResponse toResponse(Todo entity) {
        TodoResponse response = new TodoResponse();

        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setCompleted(entity.isCompleted());
        response.setDueDate(entity.getDueDate());
        response.setUserId(entity.getUser().getId());

        return response;
    }
}
