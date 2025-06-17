package com.emobile.springtodo.exception;

public class TodoRepositoryException extends RuntimeException {

    public TodoRepositoryException(String message) {
        super(message);
    }
}
