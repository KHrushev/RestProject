package com.restproject.model;

public class IncorrectDateException extends Exception {

    public IncorrectDateException(String message) {
        super(message);
    }

    public IncorrectDateException() {}
}
