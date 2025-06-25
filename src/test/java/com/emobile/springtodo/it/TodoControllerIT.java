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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.*;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class TodoControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
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
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_TODOS = "/api/v1/todos";
    private static final String URL_TODOS_USER = URL_TODOS + "/user/";
    private static final String URL_TODOS_COMPLETE = URL_TODOS + "/complete";
    private static final String URL_TODOS_INCOMPLETE = URL_TODOS + "/incomplete";

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
    void shouldGetAllTodosByUserIdWithPagination() throws Exception {
        // Given
        Todo todo1 = Todo.builder()
                .userId(USER_ID)
                .id(1L)
                .completed(true)
                .description("Second Description")
                .title("Second Todo")
                .build();

        Todo todo2 = Todo.builder()
                .userId(USER_ID)
                .id(1L)
                .completed(true)
                .description("Second Description")
                .title("Second Todo")
                .build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        // When & Then
        mockMvc.perform(get(URL_TODOS_USER + "/" +  USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
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
        MvcResult result = mockMvc.perform(get(URL_TODOS + "/" + todoId))
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
        mockMvc.perform(get(URL_TODOS + "/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update todo successfully")
    void shouldUpdateTodoSuccessfully() throws Exception {
        // Given
        Todo todo = Todo.builder()
                .userId(USER_ID)
                .completed(true)
                .description("Second Description")
                .title("Second Todo")
                .build();

        Todo todoSaved = todoRepository.save(todo).orElseThrow();
        TodoUpdateDto updateDto = TodoUpdateDto.builder()
                .title("Updated Todo")
                .description("Updated Description")
                .build();

        TodoDto dto = TodoDto.builder()
                .id(todoSaved.getId())
                .tags(null)
                .title("Updated Todo")
                .description("Updated Description")
                .completed(false)
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(put(URL_TODOS + "/" + todoSaved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JSONAssert.assertEquals(objectMapper.writeValueAsString(dto), result.getResponse().getContentAsString(),
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
        mockMvc.perform(put(URL_TODOS + "/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should complete todo successfully")
    void shouldCompleteTodoSuccessfully() throws Exception {
        // Given
        Todo todo = Todo.builder()
                .userId(USER_ID)
                .completed(false)
                .description("Second Description")
                .title("Second Todo")
                .build();

        Todo todoSaved = todoRepository.save(todo).orElseThrow();
        long todoId = todoSaved.getId();

        TodoDto dto = TodoDto.builder()
                .id(todoSaved.getId())
                .tags(null)
                .description("Second Description")
                .title("Second Todo")
                .completed(true)
                .build();

        // When & Then
        mockMvc.perform(patch(URL_TODOS_COMPLETE + "/" + todoId))
                .andExpect(status().isNoContent());

        // Verify todo is completed
        MvcResult result = mockMvc.perform(get(URL_TODOS + "/" + todoId))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals(objectMapper.writeValueAsString(dto), result.getResponse().getContentAsString(),
                JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should incomplete todo successfully")
    void shouldIncompleteTodoSuccessfully() throws Exception {
        // Given
        Todo todo = Todo.builder()
                .userId(USER_ID)
                .completed(true)
                .description("Second Description")
                .title("Second Todo")
                .build();

        Todo todoSaved = todoRepository.save(todo).orElseThrow();

        mockMvc.perform(patch(URL_TODOS_INCOMPLETE + "/" + todoSaved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when completing non-existent todo")
    void shouldReturn404WhenCompletingNonExistentTodo() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(patch(URL_TODOS_INCOMPLETE + "/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete todo successfully")
    void shouldDeleteTodoSuccessfully() throws Exception {
        // Given
        Todo todo = Todo.builder()
                .userId(USER_ID)
                .completed(true)
                .description("Second Description")
                .title("Second Todo")
                .build();

        Todo todoSaved = todoRepository.save(todo).orElseThrow();
        Long todoId = todoSaved.getId();


        // When & Then
        mockMvc.perform(delete(URL_TODOS + "/" + todoId))
                .andExpect(status().isNoContent());

        // Verify is deleted
        mockMvc.perform(get(URL_TODOS + "/" + todoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent todo")
    void shouldReturn404WhenDeletingNonExistentTodo() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(delete(URL_TODOS + "/" + nonExistentId))
                .andExpect(status().isNotFound());
    }
}


