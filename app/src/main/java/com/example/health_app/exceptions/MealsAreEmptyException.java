package com.example.health_app.exceptions;

public class MealsAreEmptyException extends RuntimeException {

    public MealsAreEmptyException(String text) {
        super(text);
    }
}
