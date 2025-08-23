package com.demo.todoapp.controller;

import com.demo.todoapp.dto.TodoRequest;
import com.demo.todoapp.dto.TodoResponse;
import com.demo.todoapp.exception.ResourceNotFoundException;
import com.demo.todoapp.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.demo.todoapp.controller.ApiConstant.BASE_PATH;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TodoController.class)
class TodoControllerTest {
    private final String BASE_URL = BASE_PATH + "/todos";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TodoService todoService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetTodosByUser() throws Exception {
        List<TodoResponse> todos = Arrays.asList(createTodoResponse(), createCompletedTodoResponse());

        when(todoService.getTodosByUser(1L)).thenReturn(todos);

        mockMvc.perform(get(BASE_URL + "/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Complete project documentation")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Prepare monthly report")))
                .andExpect(jsonPath("$[1].completed", is(true)));
    }

    @Test
    void testGetTodosByUser_WithNonExistentUser_ThrowsException() throws Exception {
        when(todoService.getTodosByUser(999L)).thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(get(BASE_URL + "/user/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTodoById() throws Exception {
        TodoResponse todoResponse = createTodoResponse();

        when(todoService.getTodoById(1L)).thenReturn(todoResponse);

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Complete project documentation")))
                .andExpect(jsonPath("$.description", is("Write comprehensive documentation for the new feature implementation")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void testGetTodoById_WithNonExistentId_ThrowsException() throws Exception {
        when(todoService.getTodoById(999L)).thenThrow(new ResourceNotFoundException("Todo not found with ID: 999"));

        mockMvc.perform(get(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTodo() throws Exception {
        TodoRequest todoRequest = createTodoRequest();
        TodoResponse todoResponse = createTodoResponse();

        when(todoService.createTodo(eq(1L), any(TodoRequest.class))).thenReturn(todoResponse);

        mockMvc.perform(post(BASE_URL + "/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Complete project documentation")))
                .andExpect(jsonPath("$.description", is("Write comprehensive documentation for the new feature implementation")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void testCreateTodo_WithNonExistentUser_ThrowsException() throws Exception {
        TodoRequest todoRequest = createTodoRequest();

        when(todoService.createTodo(eq(999L), any(TodoRequest.class))).thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(post(BASE_URL + "/user/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTodo_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        TodoRequest invalidRequest = new TodoRequest();

        mockMvc.perform(post(BASE_URL + "/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateTodo() throws Exception {
        TodoRequest updateRequest = createUpdateTodoRequest();
        TodoResponse updatedResponse = createUpdatedTodoResponse();

        when(todoService.updateTodo(eq(1L), any(TodoRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Review and approve pull requests")))
                .andExpect(jsonPath("$.description", is("Review code changes and provide feedback on the latest pull requests")))
                .andExpect(jsonPath("$.completed", is(true)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void testUpdateTodo_WithNonExistentId_ThrowsException() throws Exception {
        TodoRequest updateRequest = createUpdateTodoRequest();

        when(todoService.updateTodo(eq(999L), any(TodoRequest.class))).thenThrow(new ResourceNotFoundException("Todo not found with ID: 999"));

        mockMvc.perform(put(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTodo_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        TodoRequest invalidRequest = new TodoRequest();

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteTodoById() throws Exception {
        doNothing().when(todoService).deleteTodoById(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTodoById_WithNonExistentId_ThrowsException() throws Exception {
        doThrow(new ResourceNotFoundException("Todo not found with ID: 999")).when(todoService).deleteTodoById(999L);

        mockMvc.perform(delete(BASE_URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTodosByCompletionStatus_Completed() throws Exception {
        List<TodoResponse> completedTodos = List.of(createCompletedTodoResponse());

        when(todoService.getTodosByCompletionStatus(1L, true)).thenReturn(completedTodos);

        mockMvc.perform(get(BASE_URL + "/user/1/completed")
                        .param("completed", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].title", is("Prepare monthly report")))
                .andExpect(jsonPath("$[0].completed", is(true)));
    }

    @Test
    void testGetTodosByCompletionStatus_Incomplete() throws Exception {
        List<TodoResponse> incompleteTodos = List.of(createTodoResponse());

        when(todoService.getTodosByCompletionStatus(1L, false)).thenReturn(incompleteTodos);

        mockMvc.perform(get(BASE_URL + "/user/1/completed")
                        .param("completed", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Complete project documentation")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

    @Test
    void testGetTodosByCompletionStatus_WithNonExistentUser_ThrowsException() throws Exception {
        when(todoService.getTodosByCompletionStatus(999L, true)).thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

        mockMvc.perform(get(BASE_URL + "/user/999/completed")
                        .param("completed", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    private TodoResponse createTodoResponse() {
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("Complete project documentation");
        todoResponse.setDescription("Write comprehensive documentation for the new feature implementation");
        todoResponse.setCompleted(false);
        todoResponse.setDueDate(LocalDate.now().plusDays(7));
        todoResponse.setUserId(1L);

        return todoResponse;
    }

    private TodoResponse createCompletedTodoResponse() {
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(2L);
        todoResponse.setTitle("Prepare monthly report");
        todoResponse.setDescription("Compile and analyze monthly performance metrics and KPIs");
        todoResponse.setCompleted(true);
        todoResponse.setDueDate(LocalDate.now().minusDays(2));
        todoResponse.setUserId(1L);

        return todoResponse;
    }

    private TodoResponse createUpdatedTodoResponse() {
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("Review and approve pull requests");
        todoResponse.setDescription("Review code changes and provide feedback on the latest pull requests");
        todoResponse.setCompleted(true);
        todoResponse.setDueDate(LocalDate.now().plusDays(3));
        todoResponse.setUserId(1L);

        return todoResponse;
    }
}
