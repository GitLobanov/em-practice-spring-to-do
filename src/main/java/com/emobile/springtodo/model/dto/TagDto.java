package com.emobile.springtodo.model.dto;

import java.time.*;

public record TagDto (
        Long id,
        String title,
        String description,
        LocalDateTime createdAt
) {
}