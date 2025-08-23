package com.demo.todoapp.service.impl;

import com.demo.todoapp.dto.TodoRequest;
import com.demo.todoapp.dto.TodoResponse;
import com.demo.todoapp.entity.Todo;
import com.demo.todoapp.entity.User;
import com.demo.todoapp.exception.ResourceNotFoundException;
import com.demo.todoapp.repository.TodoRepository;
import com.demo.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TodoServiceImpl todoService;

    @Test
    void testGetTodosByUser() {
        Todo todo = createTodo();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(todoRepository.findByUserId(1L)).thenReturn(List.of(todo));

        List<TodoResponse> actual = todoService.getTodosByUser(1L);

        assertEquals(1, actual.size());
        assertEquals("Complete project documentation", actual.getFirst().getTitle());
        verify(todoRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetTodosByUser_ThrowsUserNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodosByUser(1L));
    }

    @Test
    void testGetTodoById() {
        Todo todo = createTodo();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        TodoResponse actual = todoService.getTodoById(1L);

        assertEquals("Complete project documentation", actual.getTitle());
        assertEquals("Write comprehensive documentation for the new feature implementation", actual.getDescription());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTodoById_ThrowsException() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(1L));
    }

    @Test
    void testCreateTodo() {
        User user = createUser();
        Todo savedTodo = createTodo();
        TodoRequest todoRequest = createTodoRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        TodoResponse actual = todoService.createTodo(1L, todoRequest);

        assertNotNull(actual);
        assertEquals("Complete project documentation", actual.getTitle());
        assertEquals("Write comprehensive documentation for the new feature implementation", actual.getDescription());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testCreateTodo_ThrowsUserNotFoundException() {
        TodoRequest todoRequest = createTodoRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> todoService.createTodo(1L, todoRequest));
    }

    @Test
    void testUpdateTodo() {
        Todo existingTodo = createTodo();
        Todo updatedTodo = createUpdatedTodo();
        TodoRequest updateRequest = createUpdateTodoRequest();

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

        TodoResponse actual = todoService.updateTodo(1L, updateRequest);

        assertNotNull(actual);
        assertEquals("Review and approve pull requests", actual.getTitle());
        assertEquals("Review code changes and provide feedback on the latest pull requests", actual.getDescription());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testUpdateTodo_ThrowsNotFoundException() {
        TodoRequest updateRequest = createUpdateTodoRequest();

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> todoService.updateTodo(1L, updateRequest));
    }

    @Test
    void testDeleteTodoById() {
        when(todoRepository.existsById(1L)).thenReturn(true);

        todoService.deleteTodoById(1L);

        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTodoById_ThrowsException() {
        when(todoRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> todoService.deleteTodoById(1L));
    }

    @Test
    void testGetTodosByCompletionStatus_Completed() {
        Todo completedTodo = createCompletedTodo();
        Todo incompleteTodo = createTodo();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(todoRepository.findByUserId(1L)).thenReturn(Arrays.asList(completedTodo, incompleteTodo));

        List<TodoResponse> actual = todoService.getTodosByCompletionStatus(1L, true);

        assertEquals(1, actual.size());
        assertEquals("Prepare monthly report", actual.getFirst().getTitle());
    }

    @Test
    void testGetTodosByCompletionStatus_Incomplete() {
        Todo completedTodo = createCompletedTodo();
        Todo incompleteTodo = createTodo();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(todoRepository.findByUserId(1L)).thenReturn(Arrays.asList(completedTodo, incompleteTodo));

        List<TodoResponse> actual = todoService.getTodosByCompletionStatus(1L, false);

        assertEquals(1, actual.size());
        assertEquals("Complete project documentation", actual.getFirst().getTitle());
    }

    @Test
    void testGetTodosByCompletionStatus_ThrowsUserNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodosByCompletionStatus(1L, true));
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alex.martinez");
        user.setEmail("alex.martinez@gmail.com");
        user.setPassword("SecurePass123!");

        return user;
    }

    private Todo createTodo() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Complete project documentation");
        todo.setDescription("Write comprehensive documentation for the new feature implementation");
        todo.setCompleted(false);
        todo.setDueDate(LocalDate.now().plusDays(7));
        todo.setUser(createUser());

        return todo;
    }

    private Todo createCompletedTodo() {
        Todo todo = new Todo();
        todo.setId(2L);
        todo.setTitle("Prepare monthly report");
        todo.setDescription("Compile and analyze monthly performance metrics and KPIs");
        todo.setCompleted(true);
        todo.setDueDate(LocalDate.now().minusDays(2));
        todo.setUser(createUser());

        return todo;
    }

    private Todo createUpdatedTodo() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Review and approve pull requests");
        todo.setDescription("Review code changes and provide feedback on the latest pull requests");
        todo.setCompleted(true);
        todo.setDueDate(LocalDate.now().plusDays(3));
        todo.setUser(createUser());

        return todo;
    }

    private TodoRequest createTodoRequest() {
        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("Setup development environment");
        todoRequest.setDescription("Configure local development environment for the new project");
        todoRequest.setCompleted(false);
        todoRequest.setDueDate(LocalDate.now().plusDays(5));

        return todoRequest;
    }

    private TodoRequest createUpdateTodoRequest() {
        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("Review and approve pull requests");
        todoRequest.setDescription("Review code changes and provide feedback on the latest pull requests");
        todoRequest.setCompleted(true);
        todoRequest.setDueDate(LocalDate.now().plusDays(3));

        return todoRequest;
    }
}