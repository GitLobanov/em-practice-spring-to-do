package com.emobile.springtodo.handler;

import com.emobile.springtodo.exception.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TodoRepositoryException.class)
    public ResponseEntity<String> handleTodoRepositoryException(TodoRepositoryException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TagRepositoryException.class)
    public ResponseEntity<String> handleTagRepositoryException(TagRepositoryException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}