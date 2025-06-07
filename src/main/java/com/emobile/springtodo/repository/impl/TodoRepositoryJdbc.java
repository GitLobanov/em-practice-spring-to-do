package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.repository.*;
import com.emobile.springtodo.repository.sql.*;
import lombok.*;
import org.springframework.dao.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.sql.*;
import java.time.*;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class TodoRepositoryJdbc implements TodoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Todo> save(Todo todo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            String insertSql = TodoSqlBuilder.insert();
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setString(1, todo.getTitle());
            ps.setString(2, todo.getDescription());
            ps.setBoolean(3, todo.isCompleted());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setTimestamp(5, Timestamp.valueOf(now));
            if (todo.getDueDate() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(todo.getDueDate()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            return ps;
        }, keyHolder);

        Number generatedIdNum;
        Map<String, Object> keys = keyHolder.getKeys();

        if (keys != null && keys.containsKey("id")) {
            generatedIdNum = (Number) keys.get("id");
        } else if (keyHolder.getKey() != null) {
            generatedIdNum = keyHolder.getKey();
        }
        else {
            throw new DataRetrievalFailureException("Failed to retrieve generated ID for Todo.");
        }

        Long generatedId = Objects.requireNonNull(generatedIdNum, "Generated ID cannot be null").longValue();

        todo.setId(generatedId);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);

        return Optional.of(todo);
    }

    @Override
    public Optional<Todo> update(Todo todo) {
        return Optional.empty();
    }

    @Override
    public void deleteByID(Long id) {

    }

    @Override
    public Optional<String> findByID(Long id) {
        return Optional.empty();
    }

    @Override
    public List<String> findAll(int limit, int offset) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }
}
