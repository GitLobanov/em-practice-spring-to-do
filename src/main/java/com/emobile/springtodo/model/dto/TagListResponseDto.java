package com.emobile.springtodo.model.dto;

import java.util.*;

public record TagListResponseDto(
        List<TagDto> tags,
        long total
) {

}
