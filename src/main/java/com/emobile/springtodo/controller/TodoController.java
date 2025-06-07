package com.emobile.springtodo.controller;

import com.emobile.springtodo.api.*;
import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.mapper.*;
import com.emobile.springtodo.repository.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController implements TodoApi {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Override
    public String getTodos() {
        return "";
    }

    @Override
    public TodoDto createTodo(TodoCreateDto createTodoDto) {
        return todoRepository.save(todoMapper.toEntity(createTodoDto))
                .map(todoMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Error in saving"));
    }

    @Override
    public TodoListResponseDto getAllTodos(int page, int size) {
        return null;
    }

    @Override
    public TodoDto updateTodo(Long id, TodoUpdateDto requestDto) {
        return null;
    }

    @Override
    public TodoDto getTodoById(Long id) {
        return null;
    }

    @Override
    public void deleteTodoById(Long id) {

    }
}
