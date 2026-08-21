package com.plm.plm_ai.item.exception;

public class BOMSelfReferenceException extends RuntimeException {

    public BOMSelfReferenceException(String message) {
        super(message);
    }
}