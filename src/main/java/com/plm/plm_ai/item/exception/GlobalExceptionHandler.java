package com.plm.plm_ai.exception;

import com.plm.plm_ai.item.exception.BOMDuplicateException;
import com.plm.plm_ai.item.exception.BOMSelfReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.plm.plm_ai.item.exception.BOMCircularDependencyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BOMDuplicateException.class)
    public ResponseEntity<String> handleBOMDuplicate(
            BOMDuplicateException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BOMSelfReferenceException.class)
    public ResponseEntity<String> handleBOMSelfReference(
            BOMSelfReferenceException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(
            RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BOMCircularDependencyException.class)
    public ResponseEntity<String> handleBOMCircularDependency(
            BOMCircularDependencyException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}