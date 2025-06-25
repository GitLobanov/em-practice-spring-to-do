package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.exception.*;
import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.model.mapper.*;
import com.emobile.springtodo.repository.*;
import com.emobile.springtodo.service.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TagRepository tagRepository;
    private final TodoMapper todoMapper;

    @Override
    public List<TodoDto> getTodos() {
        return todoRepository.findAll()
                .stream().map(todoMapper::toDto)
                .toList();
    }

    @Override
    @CacheEvict(value = "todoList", key = "#createTodoDto.userId")
    public TodoDto createTodo(TodoCreateDto createTodoDto) {
        Todo entity = todoMapper.toEntity(createTodoDto);
        return todoRepository.save(entity)
                .map(todoMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Error in saving"));
    }

    @Override
    @CacheEvict(value = "todo", key = "#id")
    public TodoDto updateTodo(Long id, TodoUpdateDto requestDto) {
        validateTodoById(id);
        return todoRepository.update(todoMapper.toEntity(requestDto, id))
                .map(todoMapper::toDto)
                .orElseThrow(() -> new TodoRepositoryException("Error in updating"));
    }

    @Override
    public void deleteTodo(Long id) {
        validateTodoById(id);
        todoRepository.deleteByID(id);
    }

    @Override
    public void completeTodo(Long id) {
        validateTodoById(id);
        todoRepository.completeById(id);
    }

    @Override
    public void incompleteTodo(Long id) {
        validateTodoById(id);
        todoRepository.incompleteById(id);
    }

    @Override
    public void appendTagTodo(Long todoId, Long tagId) {
        tagRepository.appendTagToTodo(todoId, tagId);
    }

    @Override
    public void removeTagTodo(Long todoId, Long tagId) {
        tagRepository.removeTagForTodo(todoId, tagId);
    }

    @Override
    @Cacheable(value = "todo", key = "#id")
    public TodoDto getTodoById(Long id) {
        return todoRepository.findByID(id)
                .map(todoMapper::toDto)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found"));
    }

    @Override
    @Cacheable(value = "todoList", key = "#userId")
    public TodoListResponseDto getAllTodosByUserId(long userId) {
        List<Todo> allByUserId = todoRepository.findAllByUserId(userId);
        List<TodoDto> list = allByUserId.stream()
                .map(todo ->
                    todoMapper.toDtoWithTags(todo, tagRepository.findByTodoId(todo.getId())))
                .toList();
        return new TodoListResponseDto (list, list.size());
    }

    @Override
    @Cacheable(value = "todoList", key = "#tagId")
    public TodoListResponseDto getAllTodosByTagId(long tagId) {
        List<TodoDto> list = todoRepository.findAllByTagId(tagId)
                .stream().map(todoMapper::toDto).toList();
        return new TodoListResponseDto (list, list.size());
    }

    void validateTodoById (Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException("Todo not found with id: " + id);
        }
    }
}
