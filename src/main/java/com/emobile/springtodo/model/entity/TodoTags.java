package com.emobile.springtodo.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoTags {

    private Long id;
    private Long todoId;
    private Long tagId;
}
