package com.emobile.springtodo.repository.sql;

import lombok.experimental.*;

@UtilityClass
public class UserSqlUtil {
    public static final String USER_SELECT_BY_ID = """
            SELECT id, username, password, email, created_at, updated_at, enabled
            FROM users 
            WHERE id = ?
            """;

    public static final String USER_SELECT_BY_USERNAME = """
            SELECT id, username, password, email, created_at, updated_at, enabled 
            FROM users 
            WHERE username = ?
            """;

    public static final String USER_SELECT_BY_EMAIL = """
            SELECT id, username, password, email, created_at, updated_at, enabled 
            FROM users 
            WHERE email = ?
            """;

    public static final String USER_SELECT_ALL = """
            SELECT id, username, password, email, created_at, updated_at, enabled 
            FROM users
            """;

    public static final String USER_SELECT_EXIST_BY_ID = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";
    public static final String USER_SELECT_EXIST_BY_USERNAME = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
    public static final String USER_SELECT_EXIST_BY_EMAIL = "SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)";

    public static final String USER_INSERT_ALL = """
            INSERT INTO users (username, password, email, created_at, updated_at, enabled)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    public static final String USER_UPDATE_ALL = """
            UPDATE users 
            SET username = ?, password = ?, email = ?, updated_at = ?, enabled = ?
            WHERE id = ?
            """;

    public static final String USER_DELETE_BY_ID = "DELETE FROM users WHERE id = ?";

    public static final String USER_COUNT_ALL = "SELECT COUNT(*) FROM users";
}
