package com.plm.plm_ai.item.exception;

public class BOMCircularDependencyException extends RuntimeException {

    public BOMCircularDependencyException(String message) {
        super(message);
    }
}