package com.myapp.app.controllers.web;

import com.myapp.app.controllers.web.responses.ErrorResponse;
import com.myapp.app.exception.business.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        "BUSINESS_ERROR",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(InvalidPathException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPath(
            InvalidPathException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        "INVALID_FILE_PATH",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(BookImportException.class)
    public ResponseEntity<ErrorResponse> handleBookImport(
            BookImportException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        "INVALID_FILE",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(OrderImportException.class)
    public ResponseEntity<ErrorResponse> handleOrderImport(
            OrderImportException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        "INVALID_FILE",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(
            BookNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        ex.getMessage(),
                        "NOT_FOUND_BOOK",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        ex.getMessage(),
                        "NOT_FOUND_ORDER",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateBook(
            DuplicateIsbnException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        ex.getMessage(),
                        "DUPLICATE_BOOK",
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(IncompatibleSortTypesException.class)
    public ResponseEntity<ErrorResponse> handleIncompatibleSortType(
            IncompatibleSortTypesException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        "INVALID_SORT_PARAMETERS",
                        LocalDateTime.now()
                )
        );
    }
}