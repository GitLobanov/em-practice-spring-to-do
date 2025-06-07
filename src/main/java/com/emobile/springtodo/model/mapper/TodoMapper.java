package com.emobile.springtodo.model.mapper;

import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodoMapper {

    TodoDto toDto(Todo todo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    Todo toEntity(TodoCreateDto requestDto);

    @Mapping(target = "id", ignore = true)
    Todo toEntity(TodoUpdateDto requestDto);
}
