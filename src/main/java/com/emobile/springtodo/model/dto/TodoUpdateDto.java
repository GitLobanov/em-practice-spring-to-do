package com.emobile.springtodo.model.dto;

public record TodoUpdateDto(
        String title,
        String description,
        Boolean completed
) {

}
