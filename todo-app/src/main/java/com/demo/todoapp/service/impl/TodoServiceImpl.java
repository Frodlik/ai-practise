package com.demo.todoapp.service.impl;

import com.demo.todoapp.dto.TodoRequest;
import com.demo.todoapp.dto.TodoResponse;
import com.demo.todoapp.entity.Todo;
import com.demo.todoapp.entity.User;
import com.demo.todoapp.exception.ResourceNotFoundException;
import com.demo.todoapp.repository.TodoRepository;
import com.demo.todoapp.repository.UserRepository;
import com.demo.todoapp.service.TodoService;
import com.demo.todoapp.util.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Override
    public List<TodoResponse> getTodosByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserId(userId);

        return todos.stream().map(TodoMapper::toResponse).toList();
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with ID: " + id));

        return TodoMapper.toResponse(todo);
    }

    @Override
    public TodoResponse createTodo(Long userId, TodoRequest todoRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Todo todo = TodoMapper.toEntity(todoRequest);
        todo.setUser(user);

        return TodoMapper.toResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoRequest todoRequest) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with ID: " + id));

        Todo updatedTodo = TodoMapper.updateEntity(existingTodo, todoRequest);

        return TodoMapper.toResponse(todoRepository.save(updatedTodo));
    }

    @Override
    public void deleteTodoById(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found with ID: " + id);
        }

        todoRepository.deleteById(id);
    }

    @Override
    public List<TodoResponse> getTodosByCompletionStatus(Long userId, boolean completed) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<Todo> todos = todoRepository.findByUserId(userId)
                .stream()
                .filter(todo -> todo.isCompleted() == completed)
                .toList();

        return todos.stream().map(TodoMapper::toResponse).toList();
    }
}
