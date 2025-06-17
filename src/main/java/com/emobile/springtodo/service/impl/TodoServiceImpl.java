package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.exception.*;
import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.model.mapper.*;
import com.emobile.springtodo.repository.*;
import com.emobile.springtodo.service.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    // TODO fix in next versions
    private final Long USE_FAKE_USER_ID = 1L;

    @Override
    public List<TodoDto> getTodos() {
        return List.of();
    }

    @Override
    public TodoDto createTodo(TodoCreateDto createTodoDto) {
        Todo entity = todoMapper.toEntity(createTodoDto);
        entity.setUserId(USE_FAKE_USER_ID);
        return todoRepository.save(entity)
                .map(todoMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Error in saving"));
    }

    @Override
    public TodoDto updateTodo(Long id, TodoUpdateDto requestDto) {
        return todoRepository.update(todoMapper.toEntity(requestDto, id))
                .map(todoMapper::toDto)
                .orElseThrow(() -> new TodoRepositoryException("Error in updating"));
    }

    @Override
    public void deleteTodo(Long id) {
        todoRepository.deleteByID(id);
    }

    @Override
    public void completeTodo(Long id) {
        todoRepository.completeById(id);
    }

    @Override
    public void incompleteTodo(Long id) {
        todoRepository.incompleteById(id);
    }

    @Override
    public Todo getTodoById(Long id) {
        return null;
    }

    @Override
    public TodoListResponseDto getAllTodosByUserId(int page, int size, long userId) {
        List<Todo> allByUserId = todoRepository.findAllByUserId(size, page * size, USE_FAKE_USER_ID);
        return todoMapper.toListResponseDto(allByUserId, todoRepository.count(), page, size);
    }
}
