package com.tingco.codechallenge.elevator.exception;

public class ElevatorException extends RuntimeException {
    private final String message;

    public ElevatorException (String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
