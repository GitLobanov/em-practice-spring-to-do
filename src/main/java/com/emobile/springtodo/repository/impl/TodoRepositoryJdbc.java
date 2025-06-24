package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.exception.*;
import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.repository.*;
import lombok.*;
import org.springframework.dao.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.sql.*;
import java.time.*;
import java.util.*;

import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_DELETE_BY_ID;
import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_INSERT_ALL;
import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_SELECT_ALL;
import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_SELECT_BY_ID;
import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_SELECT_BY_USER_ID;
import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_SELECT_EXIST_BY_ID;
import static com.emobile.springtodo.repository.sql.TodoSqlUtil.TODO_UPDATE_ALL;

@RequiredArgsConstructor
@Repository
public class TodoRepositoryJdbc implements TodoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Todo> save(Todo todo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(TODO_INSERT_ALL, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, todo.getUserId());
            ps.setString(2, todo.getTitle());
            ps.setString(3, todo.getDescription());
            ps.setBoolean(4, todo.isCompleted());
            ps.setTimestamp(5, Timestamp.valueOf(now));
            ps.setTimestamp(6, Timestamp.valueOf(now));
            if (todo.getDueDate() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(todo.getDueDate()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }
            return ps;
        }, keyHolder);

        Number generatedIdNum;
        Map<String, Object> keys = keyHolder.getKeys();

        if (keys != null && keys.containsKey("id")) {
            generatedIdNum = (Number) keys.get("id");
        } else if (keyHolder.getKey() != null) {
            generatedIdNum = keyHolder.getKey();
        } else {
            throw new TodoRepositoryException("Failed to retrieve generated ID for Todo.");
        }

        Long generatedId = Objects.requireNonNull(generatedIdNum, "Generated ID cannot be null").longValue();

        todo.setId(generatedId);
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);

        return Optional.of(todo);
    }

    @Override
    public Optional<Todo> update(Todo todo) {
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(TODO_UPDATE_ALL);
            ps.setString(1, todo.getTitle());
            ps.setString(2, todo.getDescription());
            ps.setBoolean(3, todo.isCompleted());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            if (todo.getDueDate() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(todo.getDueDate()));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }
            ps.setLong(6, todo.getId());
            return ps;
        });

        todo.setUpdatedAt(now);

        return Optional.of(todo);
    }

    @Override
    public void deleteByID(Long id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(TODO_DELETE_BY_ID);
            ps.setLong(1, id);
            return ps;
        });
    }

    @Override
    public void completeById(Long id) {
        Todo todo = findByID(id).orElseThrow(() -> new TodoRepositoryException("Todo not found with ID: " + id));
        todo.setCompleted(true);
        update(todo);
    }

    @Override
    public void incompleteById(Long id) {
        Todo todo = findByID(id).orElseThrow(() -> new TodoRepositoryException("Todo not found with ID: " + id));
        todo.setCompleted(false);
        update(todo);
    }

    @Override
    public Optional<Todo> findByID(Long id) {
        try {
            Todo todo = jdbcTemplate.queryForObject(TODO_SELECT_BY_ID, new TodoRowMapper(), id);
            return Optional.ofNullable(todo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Todo> findAll() {
        return jdbcTemplate.query(
                TODO_SELECT_ALL,
                new TodoRowMapper()
        );
    }

    @Override
    public List<Todo> findAllByUserId(long userId) {
        return jdbcTemplate.query(
                TODO_SELECT_BY_USER_ID,
                new TodoRowMapper(),
                userId
        );
    }

    @Override
    public List<Todo> findAllByTagId(int limit, int offset, long tagId) {
        return jdbcTemplate.query(
                TODO_SELECT_BY_USER_ID,
                new TodoRowMapper(),
                tagId,
                limit,
                offset
        );
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(
                jdbcTemplate.query(
                        TODO_SELECT_EXIST_BY_ID,
                        new SingleColumnRowMapper<>(Boolean.class),
                        id
                ).stream().findFirst().orElse(false)
        );
    }

    /**
     * A reusable RowMapper to map a database row to a Todo object.
     */
    private static class TodoRowMapper implements RowMapper<Todo> {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Todo todo = new Todo();
            todo.setId(rs.getLong("id"));
            todo.setUserId(rs.getLong("user_id"));
            todo.setTitle(rs.getString("title"));
            todo.setDescription(rs.getString("description"));
            todo.setCompleted(rs.getBoolean("completed"));

            Timestamp createdAtTs = rs.getTimestamp("created_at");
            if (createdAtTs != null) {
                todo.setCreatedAt(createdAtTs.toLocalDateTime());
            }

            Timestamp updatedAtTs = rs.getTimestamp("updated_at");
            if (updatedAtTs != null) {
                todo.setUpdatedAt(updatedAtTs.toLocalDateTime());
            }

            Timestamp dueDateTs = rs.getTimestamp("due_date");
            if (dueDateTs != null) {
                todo.setDueDate(dueDateTs.toLocalDateTime());
            }

            return todo;
        }
    }
}
