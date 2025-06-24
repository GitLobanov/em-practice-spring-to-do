package com.emobile.springtodo.controller;

import com.emobile.springtodo.api.*;
import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController implements TodoApi {

    private final TodoService todoService;

    @Override
    public TodoDto createTodo(TodoCreateDto createTodoDto) {
        return todoService.createTodo(createTodoDto);
    }

    @Override
    public TodoListResponseDto getAllTodosByUserId(Long userId) {
        return todoService.getAllTodosByUserId(userId);
    }

    @Override
    public TodoDto updateTodo(Long id, TodoUpdateDto requestDto) {
        return todoService.updateTodo(id, requestDto);
    }

    @Override
    public void completeTodo(Long id) {
        todoService.completeTodo(id);
    }

    @Override
    public void incompleteTodo(Long id) {
        todoService.incompleteTodo(id);
    }

    @Override
    public TodoDto getTodoById(Long id) {
        return todoService.getTodoById(id);
    }

    @Override
    public void deleteTodoById(Long id) {
        todoService.deleteTodo(id);
    }
}
