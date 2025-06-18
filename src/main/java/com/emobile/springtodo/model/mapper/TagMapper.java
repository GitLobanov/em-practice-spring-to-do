package com.emobile.springtodo.model.mapper;

import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {

    Tag toEntity(TagDto tagDto);
    Tag toEntity(TagCreateDto tagDto);
    Tag toEntity(TagUpdateDto tagDto);
    TagDto toTagDto(Tag tag);
}