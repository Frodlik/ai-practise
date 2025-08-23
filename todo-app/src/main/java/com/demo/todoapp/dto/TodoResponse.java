package com.demo.todoapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private LocalDate dueDate;
    private Long userId;
}
