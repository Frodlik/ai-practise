package com.demo.todoapp.service;

import com.demo.todoapp.dto.TodoRequest;
import com.demo.todoapp.dto.TodoResponse;

import java.util.List;

public interface TodoService {
    List<TodoResponse> getTodosByUser(Long userId);

    TodoResponse getTodoById(Long id);

    TodoResponse createTodo(Long userId, TodoRequest todoRequest);

    TodoResponse updateTodo(Long id, TodoRequest todoRequest);

    void deleteTodoById(Long id);

    List<TodoResponse> getTodosByCompletionStatus(Long userId, boolean completed);
}
