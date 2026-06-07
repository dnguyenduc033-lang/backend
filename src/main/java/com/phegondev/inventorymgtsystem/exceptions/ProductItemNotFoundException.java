package com.phegondev.inventorymgtsystem.exceptions;

public class ProductItemNotFoundException extends RuntimeException {
    public ProductItemNotFoundException(String message) {
        super(message);
    }
}
