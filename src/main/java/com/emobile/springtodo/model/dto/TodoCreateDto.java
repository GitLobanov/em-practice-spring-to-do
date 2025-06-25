package com.emobile.springtodo.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
public record TodoCreateDto(
        @NotNull
        Long userId,
        @NotBlank
        String title,
        String description) {
}
