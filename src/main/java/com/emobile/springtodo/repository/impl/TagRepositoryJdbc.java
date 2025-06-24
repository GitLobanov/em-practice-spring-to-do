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

import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_DELETE_BY_ID;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_DELETE_FROM_TODO;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_INSERT_ALL;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_INSERT_GROUP_TAGS;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_SELECT_ALL;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_SELECT_BY_ID;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_SELECT_BY_TODO_ID;
import static com.emobile.springtodo.repository.sql.TagSqlUtil.TAG_UPDATE_ALL;

@Repository
@RequiredArgsConstructor
public class TagRepositoryJdbc implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Tag> save(Tag todo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(TAG_INSERT_ALL, new String[]{"id"});
            ps.setString(1, todo.getTitle());
            ps.setString(2, todo.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(now));
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
            throw new TodoRepositoryException("Failed to retrieve generated ID for Todo.");
        }

        Long generatedId = Objects.requireNonNull(generatedIdNum, "Generated ID cannot be null").longValue();

        todo.setId(generatedId);
        todo.setCreatedAt(now);

        return Optional.of(todo);
    }

    @Override
    public Optional<Tag> update(Long idTag, Tag todo) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(TAG_UPDATE_ALL);
            ps.setString(1, todo.getTitle());
            ps.setString(2, todo.getDescription());
            ps.setLong(3, idTag);
            return ps;
        });

        return Optional.of(todo);
    }

    @Override
    public void appendTagToTodo(Long todoId, Long tagId) {
        jdbcTemplate.update(TAG_INSERT_GROUP_TAGS, todoId, tagId);
    }

    @Override
    public void removeTagForTodo(Long todoId, Long tagId) {
        jdbcTemplate.update(TAG_DELETE_FROM_TODO, todoId, tagId);
    }

    @Override
    public void deleteByID(Long id) {
        jdbcTemplate.update(TAG_DELETE_BY_ID, id);
    }

    @Override
    public Optional<Tag> findByID(Long id) {
        try {
            Tag tag = jdbcTemplate.queryForObject(TAG_SELECT_BY_ID, new TagRowMapper(), id);
            return Optional.ofNullable(tag);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Tag> findByTodoId(Long todoId) {
        return jdbcTemplate.query(
                TAG_SELECT_BY_TODO_ID,
                new TagRowMapper(),
                todoId
        );
    }

    @Override
    public List<Tag> findAll() {
        return jdbcTemplate.query(
                TAG_SELECT_ALL,
                new TagRowMapper()
        );
    }

    /**
     * A reusable RowMapper to map a database row to a Tag object.
     */
    private static class TagRowMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tag tag = new Tag();
            tag.setId(rs.getLong("id"));
            tag.setTitle(rs.getString("title"));
            tag.setDescription(rs.getString("description"));
            tag.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            Timestamp createdAtTs = rs.getTimestamp("created_at");
            if (createdAtTs != null) {
                tag.setCreatedAt(createdAtTs.toLocalDateTime());
            }
            return tag;
        }
    }
}
