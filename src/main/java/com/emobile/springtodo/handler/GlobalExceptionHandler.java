package com.emobile.springtodo.handler;

import com.emobile.springtodo.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TodoRepositoryException.class)
    public ResponseEntity<String> handleTodoRepositoryException(TodoRepositoryException ex) {
        //TODO("Implement specific handler logic")
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}