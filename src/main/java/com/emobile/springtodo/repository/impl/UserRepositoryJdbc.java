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
public class UserRepositoryJdbc implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    UserSqlUtil.USER_INSERT_ALL,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setTimestamp(5, Timestamp.valueOf(now));
            ps.setBoolean(6, user.isEnabled());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKeys().get("id") != null
                ? ((Number) keyHolder.getKeys().get("id")).longValue()
                : null;
        user.setId(generatedId);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UserSqlUtil.USER_UPDATE_ALL);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setBoolean(5, user.isEnabled());
            ps.setLong(6, user.getId());
            return ps;
        });

        user.setUpdatedAt(now);
        return Optional.of(user);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(UserSqlUtil.USER_DELETE_BY_ID, id);
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User user = jdbcTemplate.queryForObject(
                    UserSqlUtil.USER_SELECT_BY_ID,
                    new UserRowMapper(),
                    id
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            User user = jdbcTemplate.queryForObject(
                    UserSqlUtil.USER_SELECT_BY_USERNAME,
                    new UserRowMapper(),
                    username
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(
                    UserSqlUtil.USER_SELECT_BY_EMAIL,
                    new UserRowMapper(),
                    email
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(UserSqlUtil.USER_SELECT_ALL, new UserRowMapper());
    }

    @Override
    public long count() {
        Long count = jdbcTemplate.queryForObject(
                UserSqlUtil.USER_COUNT_ALL,
                Long.class
        );
        return count != null ? count : 0;
    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.query(
                UserSqlUtil.USER_SELECT_EXIST_BY_ID,
                rs -> rs.next() ? rs.getBoolean(1) : false,
                id
        ));
    }

    @Override
    public boolean existsByUsername(String username) {
        return Boolean.TRUE.equals(jdbcTemplate.query(
                UserSqlUtil.USER_SELECT_EXIST_BY_USERNAME,
                rs -> rs.next() ? rs.getBoolean(1) : false,
                username
        ));
    }

    @Override
    public boolean existsByEmail(String email) {
        return Boolean.TRUE.equals(jdbcTemplate.query(
                UserSqlUtil.USER_SELECT_EXIST_BY_EMAIL,
                rs -> rs.next() ? rs.getBoolean(1) : false,
                email
        ));
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt.toLocalDateTime());
            }

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                user.setUpdatedAt(updatedAt.toLocalDateTime());
            }

            user.setEnabled(rs.getBoolean("enabled"));
            return user;
        }
    }
}
