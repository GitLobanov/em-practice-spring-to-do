package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.entity.*;

import java.util.*;

public interface UserRepository {

    Optional<User> save(User user);
    Optional<User> update(User user);
    void deleteById(Long id);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    long count();
    boolean existsById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}