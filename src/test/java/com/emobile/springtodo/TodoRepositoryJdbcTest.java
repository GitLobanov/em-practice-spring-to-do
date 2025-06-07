package com.emobile.springtodo;

import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.repository.*;
import com.emobile.springtodo.repository.sql.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.jdbc.core.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.junit.jupiter.Container;

import java.sql.*;
import java.time.*;
import java.time.temporal.*;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TodoRepositoryJdbc Integration Tests")
class TodoRepositoryJdbcTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_todo_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:/db/changelog/db.changelog-master.xml"); // Явно указываем путь
    }

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanup() {
        jdbcTemplate.execute("DELETE FROM todos");
    }

    @Test
    @DisplayName("save() should insert a new todo and return it with generated ID and timestamps")
    void save_shouldInsertNewTodo_andReturnWithIdAndTimestamps() {
        // Arrange
        Todo newTodo = new Todo();
        newTodo.setTitle("Learn Testcontainers");
        newTodo.setDescription("Practice writing integration tests with Testcontainers.");
        newTodo.setCompleted(false);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS); // Убираем наносекунды для сравнения
        newTodo.setDueDate(dueDate);

        // Act
        Todo savedTodo = todoRepository.save(newTodo);

        // Assert - проверяем возвращенный объект
        assertNotNull(savedTodo.getId(), "Saved todo ID should not be null");
        assertTrue(savedTodo.getId() > 0, "Saved todo ID should be positive");
        assertEquals("Learn Testcontainers", savedTodo.getTitle());
        assertEquals("Practice writing integration tests with Testcontainers.", savedTodo.getDescription());
        assertFalse(savedTodo.isCompleted());
        assertNotNull(savedTodo.getCreatedAt(), "CreatedAt should be set");
        assertNotNull(savedTodo.getUpdatedAt(), "UpdatedAt should be set");
        assertEquals(savedTodo.getCreatedAt(), savedTodo.getUpdatedAt(), "CreatedAt and UpdatedAt should be equal on creation");
        assertEquals(dueDate, savedTodo.getDueDate().truncatedTo(ChronoUnit.SECONDS), "DueDate should match");

        Todo fetchedTodo = jdbcTemplate.queryForObject(
                TodoSqlBuilder.selectById(),
                (rs, rowNum) -> {
                    Todo t = new Todo();
                    t.setId(rs.getLong("id"));
                    t.setTitle(rs.getString("title"));
                    t.setDescription(rs.getString("description"));
                    t.setCompleted(rs.getBoolean("completed"));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    Timestamp dueTs = rs.getTimestamp("due_date");
                    if (dueTs != null) {
                        t.setDueDate(dueTs.toLocalDateTime());
                    }
                    return t;
                },
                savedTodo.getId()
        );

        assertNotNull(fetchedTodo, "Fetched todo should not be null");
        assertEquals(savedTodo.getId(), fetchedTodo.getId());
        assertEquals(savedTodo.getTitle(), fetchedTodo.getTitle());
        assertEquals(savedTodo.getDescription(), fetchedTodo.getDescription());
        assertEquals(savedTodo.isCompleted(), fetchedTodo.isCompleted());
        assertEquals(savedTodo.getCreatedAt().truncatedTo(ChronoUnit.SECONDS), fetchedTodo.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(savedTodo.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS), fetchedTodo.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        if (savedTodo.getDueDate() != null && fetchedTodo.getDueDate() != null) {
            assertEquals(savedTodo.getDueDate().truncatedTo(ChronoUnit.SECONDS), fetchedTodo.getDueDate().truncatedTo(ChronoUnit.SECONDS));
        } else {
            assertNull(savedTodo.getDueDate());
            assertNull(fetchedTodo.getDueDate());
        }
    }
}
