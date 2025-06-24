package com.emobile.springtodo.model.entity;

import lombok.*;

import java.time.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
}
