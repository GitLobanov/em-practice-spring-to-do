package com.emobile.springtodo.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
public record TodoCreateDto(
        Long userId,
        @NotBlank
        String title,
        String description) {
}
