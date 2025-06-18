package com.emobile.springtodo.controller;

import com.emobile.springtodo.api.*;
import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.service.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TagController implements TagApi {

    private final TagService tagService;

    @Override
    public TagDto createTag(TagCreateDto createTodoDto) {
        return tagService.createTodo(createTodoDto);
    }

    @Override
    public TagDto updateTag(Long id, @Valid TagUpdateDto requestDto) {
        return tagService.updateTodo(id, requestDto);
    }

    @Override
    public void deleteTagById(Long id) {
        tagService.removeTodo(id);
    }

    @Override
    public TagDto getTagById(Long id) {
        return tagService.getById(id);
    }

    @Override
    public void appendTagForTodo(Long id, Long tagId) {
        tagService.appendTagTodo(id, tagId);
    }

    @Override
    public void removeTagForTodo(Long id, Long tagId) {
        tagService.removeTagTodo(id, tagId);
    }
}
