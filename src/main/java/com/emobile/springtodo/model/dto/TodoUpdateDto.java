package com.emobile.springtodo.model.dto;

import lombok.*;

@Builder
public record TodoUpdateDto(
        String title,
        String description,
        Boolean completed
) {

}
