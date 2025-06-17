package com.emobile.springtodo.api;

import com.emobile.springtodo.model.dto.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.HttpStatus.CREATED;


@RequestMapping("/api/v1/todos")
public interface TodoApi {

    @RequestMapping("/")
    List<TodoDto> getTodos();

    @PostMapping
    @ResponseStatus(CREATED)
    TodoDto createTodo(@Valid @RequestBody TodoCreateDto createTodoDto);

    @GetMapping("/user/{userId}")
    TodoListResponseDto getAllTodosByUserId(
            @Parameter(description = "Page number, starts from 0", required = true)
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of the page, defaults to 10", required = true)
            @RequestParam(value = "size", defaultValue = "10") int size,
            @PathVariable @Parameter(description = "ID of the user to retrieve tasks for") long userId
    );

    @PutMapping("/{id}")
    TodoDto updateTodo(@Parameter(description = "ID of the task to update") @PathVariable Long id,
                           @Valid @RequestBody TodoUpdateDto requestDto);

    @PutMapping("/{id}/complete")
    void completeTodo(@Parameter(description = "ID of the task to complete") @PathVariable Long id);

    @PutMapping("/{id}/incomplete")
    void incompleteTodo(@Parameter(description = "ID of the task to mark incomplete") @PathVariable Long id);

    @GetMapping("/{id}")
    TodoDto getTodoById(@Parameter(description = "ID of the task to retrieve") @PathVariable Long id);

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    void deleteTodoById(@Parameter(description = "ID of the TODO item to delete") @PathVariable Long id);
}
