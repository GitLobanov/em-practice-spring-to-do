package com.emobile.springtodo.api;

import com.emobile.springtodo.model.dto.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.*;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RequestMapping("/api/v1/tags")
public interface TagApi {

    @PostMapping
    @ResponseStatus(CREATED)
    TagDto createTag(@Valid @RequestBody TagCreateDto createTodoDto);

    @PutMapping("/{id}")
    TagDto updateTag(@Parameter(description = "ID of the tag to update") @PathVariable Long id,
                      @Valid @RequestBody TagUpdateDto requestDto);

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    void deleteTagById(@Parameter(description = "ID of the tag item to delete") @PathVariable Long id);

    @GetMapping("/{id}")
    TagDto getTagById(@Parameter(description = "ID of the tag to retrieve") @PathVariable Long id);

    @PutMapping("/{id}/tag/{tagId}")
    void appendTagForTodo(
            @PathVariable @Parameter(description = "ID of the task to append") Long id,
            @PathVariable @Parameter(description = "ID of the tag to append to the task") Long tagId);

    @DeleteMapping("/{id}/tag/{tagId}")
    void removeTagForTodo(
            @PathVariable @Parameter(description = "ID of the task to append") Long id,
            @PathVariable @Parameter(description = "ID of the tag to append to the task") Long tagId);
}
