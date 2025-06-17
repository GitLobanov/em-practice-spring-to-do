package com.emobile.springtodo.service;

import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public interface TodoService {

    List<TodoDto> getTodos ();
    TodoDto createTodo (TodoCreateDto createTodoDto);
    TodoDto updateTodo (Long id, TodoUpdateDto requestDto);
    void deleteTodo (Long id);
    void completeTodo (Long id);
    void incompleteTodo (Long id);
    Todo getTodoById (Long id);
    TodoListResponseDto getAllTodosByUserId (int page, int size, long userId);
}
