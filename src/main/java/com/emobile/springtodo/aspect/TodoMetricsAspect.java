package com.emobile.springtodo.aspect;

import com.emobile.springtodo.model.entity.*;
import com.emobile.springtodo.repository.*;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Aspect
@Component
@Slf4j
public class TodoMetricsAspect {

    private final MeterRegistry meterRegistry;
    private final TodoRepository todoRepository;

    private final Counter todosCreatedCounter;
    private final Counter todosCompletedCounter;
    private final Counter todosDeletedCounter;
    private final Counter tagsAppendedCounter;

    public TodoMetricsAspect(MeterRegistry meterRegistry, TodoRepository todoRepository) {
        this.meterRegistry = meterRegistry;
        this.todoRepository = todoRepository;

        this.todosCreatedCounter = Counter.builder("todos.created.total")
                .description("Total number of created todos")
                .register(meterRegistry);

        this.todosCompletedCounter = Counter.builder("todos.completed.total")
                .description("Total number of completed todos")
                .register(meterRegistry);

        this.todosDeletedCounter = Counter.builder("todos.deleted.total")
                .description("Total number of deleted todos")
                .register(meterRegistry);

        this.tagsAppendedCounter = Counter.builder("todos.tags.appended.total")
                .description("Total number of tags appended to todos")
                .register(meterRegistry);

        Gauge.builder("todos.active.count", todoRepository, repo -> repo.countByCompleted(false))
                .description("Current number of active (incomplete) todos")
                .register(meterRegistry);

        Gauge.builder("todos.completed.count", todoRepository, repo -> repo.countByCompleted(true))
                .description("Current number of completed todos")
                .register(meterRegistry);
    }

    // --- Определения срезов (Pointcuts) ---

    @Pointcut("execution(* com.emobile.springtodo.service.impl.TodoServiceImpl.createTodo(..))")
    public void createTodoPointcut() {}

    @Pointcut("execution(* com.emobile.springtodo.service.impl.TodoServiceImpl.completeTodo(..))")
    public void completeTodoPointcut() {}

    @Pointcut("execution(* com.emobile.springtodo.service.impl.TodoServiceImpl.deleteTodo(..))")
    public void deleteTodoPointcut() {}

    @Pointcut("execution(* com.emobile.springtodo.service.impl.TodoServiceImpl.appendTagTodo(..))")
    public void appendTagTodoPointcut() {}


    // --- Советы (Advices) ---

    @AfterReturning("createTodoPointcut()")
    public void afterTodoCreated() {
        todosCreatedCounter.increment();
    }

    @AfterReturning("deleteTodoPointcut()")
    public void afterTodoDeleted() {
        todosDeletedCounter.increment();
    }

    @AfterReturning("appendTagTodoPointcut()")
    public void afterTagAppended() {
        tagsAppendedCounter.increment();
    }

    /**
     * Самая интересная метрика: измеряем время от создания до завершения задачи.
     * Используем @Around, чтобы получить доступ к аргументам метода и выполнить
     * действия до и после его вызова.
     */
    @Around("completeTodoPointcut()")
    public Object measureCompletionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Long todoId = (Long) joinPoint.getArgs()[0];

        // Получаем задачу ДО ее завершения, чтобы узнать время создания
        Optional<Todo> todoOptional = todoRepository.findByID(todoId);

        // Выполняем оригинальный метод completeTodo()
        Object result = joinPoint.proceed();

        // ПОСЛЕ успешного выполнения метода...
        if (todoOptional.isPresent()) {
            Todo todo = todoOptional.get();
            if (todo.getCreatedAt() != null) {
                // Увеличиваем счетчик завершенных задач
                todosCompletedCounter.increment();

                // Рассчитываем и записываем время выполнения
                Duration timeToComplete = Duration.between(todo.getCreatedAt(), LocalDateTime.now());
                Timer.builder("todos.completion.time")
                        .description("Time taken to complete a todo from its creation")
                        .register(meterRegistry)
                        .record(timeToComplete);
                log.info("Todo {} completed in {}", todoId, timeToComplete);
            }
        }

        return result;
    }
}