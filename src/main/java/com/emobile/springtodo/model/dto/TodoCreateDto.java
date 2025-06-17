package com.emobile.springtodo.model.dto;

public record TodoCreateDto(

        Long userId,
        String title,
        String description) {

}
