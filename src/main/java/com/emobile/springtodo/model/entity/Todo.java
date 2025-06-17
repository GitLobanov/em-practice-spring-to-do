package com.emobile.springtodo.model.entity;

/**
 * Create a class entity called Todo with the following fields:
 * id (Long, Primary Key, автоинкремент)
 * title (String, не null, макс. длина, например, 255)
 * description (String, опционально, text)
 * completed (boolean, по умолчанию false)
 * createdAt (Timestamp, не null, по умолчанию текущее время)
 * updatedAt (Timestamp, не null, по умолчанию текущее время, обновляется при изменении)
 * dueDate (Date/Timestamp, опционально, для инновационной функции)
 */

import lombok.*;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private boolean completed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime dueDate;
}
