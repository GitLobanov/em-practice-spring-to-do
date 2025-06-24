package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.entity.*;

import java.util.*;

public interface TagRepository {

    Optional<Tag> save(Tag todo);
    Optional<Tag> update(Long idTag, Tag todo);
    void appendTagToTodo(Long todoId, Long tagId);
    void removeTagForTodo(Long todoId, Long tagId);
    void deleteByID(Long id);
    Optional<Tag> findByID(Long id);
    List<Tag> findByTodoId(Long todoId);
    List<Tag> findAll();
}
