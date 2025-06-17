package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.entity.*;

import java.util.*;

public interface TodoRepository {

    Optional<Todo> save(Todo todo);
    Optional<Todo> update(Todo todo);
    void deleteByID(Long id);
    void completeById(Long id);
    void incompleteById(Long id);
    Optional<Todo> findByID(Long id);
    List<Todo> findAll(int limit, int offset);
    List<Todo> findAllByUserId(int limit, int offset, long userId);
    long count();

}
