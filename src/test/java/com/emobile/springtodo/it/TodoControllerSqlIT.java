package com.emobile.springtodo.it;

import com.emobile.springtodo.model.dto.TodoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class TodoControllerSqlIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12-alpine")
            .withDatabaseName("todo_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_TODOS = "/api/v1/todos";
    private static final String URL_TODOS_USER = URL_TODOS + "/user/";

    @Test
    @DisplayName("Should get todo by ID successfully using @Sql setup")
    @Sql(scripts = {
            "/sql/insert/insert-test-users.sql",
            "/sql/insert/insert-test-todos.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/truncate/truncate-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGetTodoByIdSuccessfully() throws Exception {
        // Given
        long todoId = 1L;

        TodoDto expectedResponse = TodoDto.builder()
                .id(todoId)
                .title("First Todo")
                .description("First Description")
                .completed(false)
                .tags(null)
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(get(URL_TODOS + "/" + todoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(
                objectMapper.writeValueAsString(expectedResponse),
                result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT // Игнорируем поля, которых нет в expected (createdAt, etc.)
        );
    }

    @Test
    @DisplayName("Should get all todos by user ID with pagination using @Sql setup")
    @Sql(scripts = {
            "/sql/insert/insert-test-users.sql",
            "/sql/insert/insert-test-todos.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/truncate/truncate-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGetAllTodosByUserIdWithPagination() throws Exception {
        // Given у пользователя с id = 1 есть 3 задачи.
        long userId = 1L;

        // When & Then
        mockMvc.perform(get(URL_TODOS_USER + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.todos.length()").value(3))
                .andExpect(jsonPath("$.todos[0].title").value("First Todo"))
                .andExpect(jsonPath("$.todos[1].title").value("Second Todo"));
    }
}