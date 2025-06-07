package com.emobile.springtodo.model.dto;

import java.util.*;

public record TodoListResponseDto(
        List<TodoDto> todos,
        long total,
        int limit,
        int offset
) {

}
