package com.emobile.springtodo.service;

import com.emobile.springtodo.model.dto.*;

public interface TagService {

    TagDto createTodo (TagCreateDto createTodoDto);
    TagDto updateTodo (Long id, TagUpdateDto requestDto);
    void removeTodo (Long id);
    TagDto getById (Long id);
    void appendTagTodo (Long todoId, Long tagId);
    void removeTagTodo (Long todoId, Long tagId);
    TagListResponseDto getAllTags();
}
