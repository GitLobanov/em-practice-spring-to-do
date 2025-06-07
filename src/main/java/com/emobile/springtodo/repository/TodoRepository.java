package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.entity.*;

import java.util.*;

public interface TodoRepository {

    Optional<Todo> save(Todo todo);
    Optional<Todo> update(Todo todo);
    void deleteByID(Long id);
    Optional<String> findByID(Long id);
    List<String> findAll(int limit, int offset);
    long count();

}
