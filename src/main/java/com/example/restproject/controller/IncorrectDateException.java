package com.example.restproject.controller;

public class IncorrectDateException extends Exception {

    public IncorrectDateException(String message) {
        super(message);
    }

    public IncorrectDateException() {}
}
