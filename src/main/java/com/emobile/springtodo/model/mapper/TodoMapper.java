package com.emobile.springtodo.model.mapper;

import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import org.mapstruct.*;

import java.util.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodoMapper {

    TodoDto toDto(Todo todo);
    @Mapping(target = "tags", source = "tags")
    TodoDto toDtoWithTags(Todo todo, List<Tag> tags);
    TodoListResponseDto toListResponseDto(List<Todo> todos, long total, int limit, int offset);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    Todo toEntity(TodoCreateDto requestDto);

    Todo toEntity(TodoUpdateDto requestDto, Long id);
}
