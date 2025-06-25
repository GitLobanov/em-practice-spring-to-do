package com.emobile.springtodo.repository.sql;

import lombok.experimental.*;

@UtilityClass
public class TodoSqlUtil {

    private static final String BASE_SELECT = """
            SELECT todos.id, user_id, todos.title, todos.description, completed, todos.created_at, updated_at, due_date
            FROM todos
            """;

    private static final String WITH_TAGS_SELECT = """
            SELECT
                todos.id,
                user_id,
                todos.title,
                todos.description,
                completed,
                todos.created_at,
                updated_at,
                due_date,
                ARRAY_AGG(tags.title) AS tags
            FROM todos
            LEFT JOIN todo_tags ON todos.id = todo_tags.todo_id
            LEFT JOIN tags ON todo_tags.tag_id = tags.id
            %s
            GROUP BY todos.id
            """;

    public static final String TODO_SELECT_BY_ID = String.format(WITH_TAGS_SELECT, "WHERE todos.id = ?");
    public static final String TODO_SELECT_BY_USER_ID = String.format(WITH_TAGS_SELECT, "WHERE user_id = ?");
    public static final String TODO_SELECT_BY_TAG_ID = String.format(WITH_TAGS_SELECT, "WHERE tags.id = ?");
    public static final String TODO_SELECT_ALL = String.format(WITH_TAGS_SELECT, "");
    public static final String TODO_SELECT_EXIST_BY_ID = "SELECT EXISTS(SELECT 1 FROM todos WHERE id = ?)";
    public static final String TODO_SELECT_BY_COMPLETED = "SELECT COUNT(*) FROM todos WHERE completed = ?";

    public static final String TODO_INSERT_ALL = """
                INSERT INTO todos (user_id, title, description, completed, created_at, updated_at, due_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

    public static final String TODO_UPDATE_ALL = """
                UPDATE todos
                SET title = ?, description = ?, completed = ?, updated_at = ?, due_date = ? 
                WHERE id = ?
                """;

    public static final String TODO_DELETE_BY_ID = "DELETE FROM todos WHERE id = ?";

    public static final String TODO_COUNT_BY_USER_ID = "SELECT COUNT(*) FROM todos WHERE user_id = ?";
    public static final String TODO_COUNT_ALL = "SELECT COUNT(*) FROM todos";
    public static String TODO_COUNT_OVERDUE_BY_USER_ID = "SELECT COUNT(*) FROM todos WHERE user_id = ? " +
            "AND due_date < ? AND completed = false";
}
