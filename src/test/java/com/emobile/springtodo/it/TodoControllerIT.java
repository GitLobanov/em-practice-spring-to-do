package com.emobile.springtodo.it;


import com.emobile.springtodo.model.dto.*;
import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.*;
import org.springframework.test.context.jdbc.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
//@AutoConfigureWebMvc
@AutoConfigureMockMvc
@Testcontainers
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
//@Sql(value = "/sql/truncate/truncate-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TodoControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("todo_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

//    @Container
//    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
//            .withExposedPorts(6379)
//            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // registry.add("spring.redis.host", redis::getHost);
        // registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_TODOS = "/api/v1/todos";
    private static final String URL_TODOS_ID = URL_TODOS + "/{id}";
    private static final String URL_TODOS_ID_COMPLETE = URL_TODOS_ID + "/complete";
    private static final String URL_TODOS_ID_INCOMPLETE = URL_TODOS_ID + "/incomplete";

    private static Long USER_ID;
    private static Long USER_ID_2;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .id(USER_ID)
                .username("ranob")
                .password("password")
                .email("ranob@example.com")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .id(USER_ID_2)
                .username("John Doe")
                .password("password1123")
                .email("johndoe@example.com")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User userSaved1 = userRepository.save(user1).orElseThrow();
        User userSaved2 = userRepository.save(user2).orElseThrow();

        USER_ID = userSaved1.getId();
        USER_ID_2 = userSaved2.getId();
    }

    @Test
    @DisplayName("Should create new todo successfully")
    void shouldCreateTodoSuccessfully() throws Exception {
        // Given
        TodoCreateDto createDto = TodoCreateDto.builder()
                .title("Test Todo")
                .description("Test Description")
                .userId(1L)
                .build();

        TodoDto expectedResponse = TodoDto.builder()
                .id(1L)
                .tags(null)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post(URL_TODOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(objectMapper.writeValueAsString(expectedResponse), result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should return validation error when creating todo with invalid data")
    void shouldReturnValidationErrorWhenCreatingTodoWithInvalidData() throws Exception {
        // Given
        TodoCreateDto createDto = TodoCreateDto.builder()
                .title("") // Invalid empty title
                .description("Test Description")
                .userId(1L)
                .build();

        // When & Then
        mockMvc.perform(post(URL_TODOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all todos by user ID with pagination")
    @Sql("/sql/insert/insert-test-todos.sql")
    void shouldGetAllTodosByUserIdWithPagination() throws Exception {
        // Given
        Long userId = 1L;
        int page = 0;
        int size = 2;

        String expectedResponse = """
                {
                    "todos": [
                        {
                            "id": 1,
                            "title": "First Todo",
                            "description": "First Description",
                            "completed": false,
                            "userId": 1
                        },
                        {
                            "id": 2,
                            "title": "Second Todo",
                            "description": "Second Description",
                            "completed": true,
                            "userId": 1
                        }
                    ],
                    "totalElements": 3,
                    "totalPages": 2,
                    "currentPage": 0,
                    "pageSize": 2
                }
                """;

        // When & Then
        MvcResult result = mockMvc.perform(get(URL_TODOS)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(expectedResponse, result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should get todo by ID successfully")
    void shouldGetTodoByIdSuccessfully() throws Exception {
        // Given
        Todo todo = Todo.builder()
                .userId(USER_ID)
                .title("First Todo")
                .description("First Description")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Todo todoSaved = todoRepository.save(todo).orElseThrow();
        Long todoId = todoSaved.getId();

        TodoDto expectedResponse = TodoDto.builder()
                .id(todoId)
                .tags(null)
                .title("First Todo")
                .description("First Description")
                .completed(false)
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(get(URL_TODOS_ID, todoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(objectMapper.writeValueAsString(expectedResponse), result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent todo")
    void shouldReturn404WhenGettingNonExistentTodo() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(get(URL_TODOS_ID, nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update todo successfully")
    @Sql("/sql/insert/insert-test-todos.sql")
    void shouldUpdateTodoSuccessfully() throws Exception {
        // Given
        Long todoId = 1L;
        TodoUpdateDto updateDto = TodoUpdateDto.builder()
                .title("Updated Todo")
                .description("Updated Description")
                .build();

        String expectedResponse = """
                {
                    "id": 1,
                    "title": "Updated Todo",
                    "description": "Updated Description",
                    "completed": false,
                    "userId": 1
                }
                """;

        // When & Then
        MvcResult result = mockMvc.perform(put(URL_TODOS_ID, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(expectedResponse, result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent todo")
    void shouldReturn404WhenUpdatingNonExistentTodo() throws Exception {
        // Given
        Long nonExistentId = 999L;
        TodoUpdateDto updateDto = TodoUpdateDto.builder()
                .title("Updated Todo")
                .description("Updated Description")
                .build();

        // When & Then
        mockMvc.perform(put(URL_TODOS_ID, nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should complete todo successfully")
    @Sql("/sql/insert/insert-test-todos.sql")
    void shouldCompleteTodoSuccessfully() throws Exception {
        // Given
        Long todoId = 1L;

        // When & Then
        mockMvc.perform(patch(URL_TODOS_ID + "/complete", todoId))
                .andExpect(status().isNoContent());

        // Verify todo is completed
        String expectedResponse = """
                {
                    "id": 1,
                    "title": "First Todo",
                    "description": "First Description",
                    "completed": true,
                    "userId": 1
                }
                """;

        MvcResult result = mockMvc.perform(get("/api/v1/todos/{id}", todoId))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals(expectedResponse, result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should incomplete todo successfully")
    @Sql("/sql/insert/insert-test-todos.sql")
    void shouldIncompleteTodoSuccessfully() throws Exception {
        // Given
        Long todoId = 2L; // This is completed in test data

        // When & Then
        mockMvc.perform(patch("/api/v1/todos/{id}/incomplete", todoId))
                .andExpect(status().isNoContent());

        // Verify is incompleted
        String expectedResponse = """
                {
                    "id": 2,
                    "title": "Second Todo",
                    "description": "Second Description",
                    "completed": false,
                    "userId": 1
                }
                """;

        MvcResult result = mockMvc.perform(get("/api/v1/todos/{id}", todoId))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals(expectedResponse, result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should return 404 when completing non-existent todo")
    void shouldReturn404WhenCompletingNonExistentTodo() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(patch(URL_TODOS_ID_COMPLETE, nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete todo successfully")
    @Sql("/sql/insert/insert-test-todos.sql")
    void shouldDeleteTodoSuccessfully() throws Exception {
        // Given
        Long todoId = 1L;

        // When & Then
        mockMvc.perform(delete(URL_TODOS_ID, todoId))
                .andExpect(status().isNoContent());

        // Verify is deleted
        mockMvc.perform(get(URL_TODOS_ID, todoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent todo")
    void shouldReturn404WhenDeletingNonExistentTodo() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(delete(URL_TODOS_ID, nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle invalid request parameters gracefully")
    void shouldHandleInvalidRequestParametersGracefully() throws Exception {
        // When & Then
        mockMvc.perform(get(URL_TODOS)
                        .param("page", "-1")
                        .param("size", "0")
                        .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return empty list when user has no todos")
    void shouldReturnEmptyListWhenUserHasNoTodos() throws Exception {
        // Given
        Long userId = 999L; // User with no todos

        String expectedResponse = """
                {
                    "todos": [],
                    "totalElements": 0,
                    "totalPages": 0,
                    "currentPage": 0,
                    "pageSize": 10
                }
                """;

        // When & Then
        MvcResult result = mockMvc.perform(get(URL_TODOS)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals(expectedResponse, result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }
}


