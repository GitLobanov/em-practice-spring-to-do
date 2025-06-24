package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.mapper.*;
import com.emobile.springtodo.repository.*;
import com.emobile.springtodo.service.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagDto createTodo(TagCreateDto createTodoDto) {
        return tagRepository.save(tagMapper.toEntity(createTodoDto))
                .map(tagMapper::toTagDto)
                .orElseThrow(() -> new RuntimeException("Failed to create tag"));
    }

    @Override
    public TagDto updateTodo(Long id, TagUpdateDto requestDto) {
        return tagRepository.update(id, tagMapper.toEntity(requestDto))
                .map(tagMapper::toTagDto)
                .orElseThrow(() -> new RuntimeException("Failed to create tag"));
    }

    @Override
    public void removeTodo(Long id) {
        tagRepository.deleteByID(id);
    }

    @Override
    public TagDto getById(Long id) {
        return tagRepository.findByID(id)
                   .map(tagMapper::toTagDto)
                   .orElseThrow(() -> new RuntimeException("Failed to find tag by id"));
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
    public TagListResponseDto getAllTags() {
        List<TagDto> tags = tagMapper.toListDto(tagRepository.findAll());
        return new TagListResponseDto(tags, tags.size());
    }
}
