package com.emobile.springtodo.model.dto;

import lombok.*;

import java.util.*;

@Builder
public record TodoDto(
        Long id,
        List<TagDto> tags,
        String title,
        String description,
        boolean completed) {
}
