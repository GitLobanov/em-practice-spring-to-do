package com.emobile.springtodo.repository.sql;

import lombok.experimental.*;

@UtilityClass
public class TodoSqlUtil {

    private static final String BASE_SELECT = """
            SELECT id, user_id, title, description, completed, created_at, updated_at, due_date
            FROM todos
            JOIN todo_tags ON todos.id = todo_tags.todo_id
            JOIN tags ON todo_tags.tag_id = tags.id
            """;

    public static final String TODO_SELECT_ALL = BASE_SELECT + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
    public static final String TODO_SELECT_BY_ID = BASE_SELECT + " WHERE id = ?";
    public static final String TODO_SELECT_BY_USER_ID = BASE_SELECT + " WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    public static final String TODO_SELECT_BY_TAG_ID = BASE_SELECT +
            "JOIN  WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    public static final String TODO_SELECT_OVERDUE = BASE_SELECT + " WHERE due_date < ? AND completed = false";
    public static final String TODO_SELECT_COMPLETED = BASE_SELECT + " WHERE completed = true";

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
