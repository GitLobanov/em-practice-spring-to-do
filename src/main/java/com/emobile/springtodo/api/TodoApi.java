package com.emobile.springtodo.api;

import com.emobile.springtodo.model.dto.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.*;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/v1/todos")
public interface TodoApi {

    @RequestMapping("/")
    String getTodos();


    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    TodoDto createTodo(@Valid @RequestBody TodoCreateDto createTodoDto);

    @GetMapping
    TodoListResponseDto getAllTodos(
            @Parameter(description = "Page number, starts from 0", required = true)
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of the page, defaults to 10", required = true)
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @PutMapping("/{id}")
    TodoDto updateTodo(@Parameter(description = "ID of the task to update") @PathVariable Long id,
                           @Valid @RequestBody TodoUpdateDto requestDto);

    @GetMapping("/{id}")
    TodoDto getTodoById(@Parameter(description = "ID of the task to retrieve") @PathVariable Long id);

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    void deleteTodoById(@Parameter(description = "ID of the TODO item to delete") @PathVariable Long id);

}
