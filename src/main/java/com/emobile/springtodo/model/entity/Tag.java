package com.emobile.springtodo.model.entity;


import lombok.*;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}
