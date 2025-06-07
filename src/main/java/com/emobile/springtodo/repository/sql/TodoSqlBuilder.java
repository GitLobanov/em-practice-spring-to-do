package com.emobile.springtodo.repository.sql;

import lombok.experimental.*;

@UtilityClass
public class TodoSqlBuilder {

    private static final String BASE_SELECT = """
            SELECT id, title, description, completed, created_at, updated_at, due_date 
            FROM todos
            """;

    public static String selectAll() {
        return BASE_SELECT + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
    }

    public static String selectById() {
        return BASE_SELECT + " WHERE id = ?";
    }

    public static String selectOverdue() {
        return BASE_SELECT + " WHERE due_date < ? AND completed = false";
    }

    public static String insert() {
        return """
                INSERT INTO todos (title, description, completed, created_at, updated_at, due_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
    }

    public static String update() {
        return """
                UPDATE todos 
                SET title = ?, description = ?, completed = ?, updated_at = ?, due_date = ? 
                WHERE id = ?
                """;
    }

    public static String delete() {
        return "DELETE FROM todos WHERE id = ?";
    }

    public static String count() {
        return "SELECT COUNT(*) FROM todos";
    }

    public static String countOverdue() {
        return "SELECT COUNT(*) FROM todos WHERE due_date < ? AND completed = false";
    }
}
