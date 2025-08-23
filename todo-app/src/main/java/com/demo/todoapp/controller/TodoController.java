package com.demo.todoapp.controller;

import com.demo.todoapp.dto.TodoRequest;
import com.demo.todoapp.dto.TodoResponse;
import com.demo.todoapp.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.demo.todoapp.controller.ApiConstant.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TodoResponse>> getTodosByUser(@PathVariable Long userId) {
        List<TodoResponse> todos = todoService.getTodosByUser(userId);

        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        TodoResponse todo = todoService.getTodoById(id);

        return ResponseEntity.ok(todo);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<TodoResponse> createTodo(@PathVariable Long userId, @Valid @RequestBody TodoRequest todoRequest) {
        TodoResponse createdTodo = todoService.createTodo(userId, todoRequest);

        return ResponseEntity.ok(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest todoRequest) {
        TodoResponse updatedTodo = todoService.updateTodo(id, todoRequest);

        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long id) {
        todoService.deleteTodoById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<TodoResponse>> getTodosByCompletionStatus(@PathVariable Long userId, @RequestParam boolean completed) {
        List<TodoResponse> todos = todoService.getTodosByCompletionStatus(userId, completed);

        return ResponseEntity.ok(todos);
    }
}
