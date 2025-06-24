package com.emobile.springtodo.api;

import com.emobile.springtodo.model.dto.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.*;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;


@RequestMapping("/api/v1/todos")
public interface TodoApi {

    @PostMapping
    @ResponseStatus(CREATED)
    TodoDto createTodo(@Valid @RequestBody TodoCreateDto createTodoDto);

    @PutMapping("/{id}")
    TodoDto updateTodo(@Parameter(description = "ID of the task to update") @PathVariable Long id,
                           @Valid @RequestBody TodoUpdateDto requestDto);

    @GetMapping("/{id}")
    TodoDto getTodoById(@Parameter(description = "ID of the task to retrieve") @PathVariable (value = "id") Long id);

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    void deleteTodoById(@Parameter(description = "ID of the TODO item to delete") @PathVariable Long id);

    @GetMapping("/user/{userId}")
    TodoListResponseDto getAllTodosByUserId(
            @PathVariable (value = "userId")
            Long userId
    );

    @PutMapping("/{id}/complete")
    void completeTodo(@Parameter(description = "ID of the task to complete") @PathVariable Long id);

    @PutMapping("/{id}/incomplete")
    void incompleteTodo(@Parameter(description = "ID of the task to mark incomplete") @PathVariable Long id);

}
