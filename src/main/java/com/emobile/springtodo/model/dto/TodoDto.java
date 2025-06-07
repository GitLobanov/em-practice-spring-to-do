package com.emobile.springtodo.model.dto;

public record TodoDto(
        Long id,
        String title,
        String description,
        boolean completed) {
}
