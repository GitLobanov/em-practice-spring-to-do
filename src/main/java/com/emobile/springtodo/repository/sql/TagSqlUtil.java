package com.emobile.springtodo.repository.sql;

import lombok.experimental.*;

@UtilityClass
public class TagSqlUtil {

    private static final String BASE_SELECT = """
            SELECT ta.id, ta.title, ta.description, ta.created_at
            FROM tags ta
            """;

    public static final String TAG_SELECT_ALL = BASE_SELECT + " ORDER BY created_at DESC";
    public static final String TAG_SELECT_BY_ID = BASE_SELECT + " WHERE id = ?";
    public static final String TAG_SELECT_BY_TODO_ID = BASE_SELECT + """
            JOIN todo_tags tt ON ta.id = tt.tag_id
            JOIN todos tod ON tt.todo_id = tod.id
            WHERE tod.id = ?
            ORDER BY ta.created_at DESC
            """;

    public static final String TAG_INSERT_ALL = """
                INSERT INTO tags (title, description, created_at)
                VALUES (?, ?, ?)
                """;

    public static final String TAG_INSERT_GROUP_TAGS = """
            INSERT INTO todo_tags (todo_id, tag_id)
            VALUES (?, ?)
            """;

    public static final String TAG_UPDATE_ALL = """
                UPDATE tags
                SET title = ?, description = ?
                WHERE id = ?
                """;

    public static final String TAG_DELETE_BY_ID = "DELETE FROM tags WHERE id = ?";
    public static final String TAG_DELETE_FROM_TODO = "DELETE FROM todo_tags WHERE tag_id = ? AND todo_id = ?";
}
