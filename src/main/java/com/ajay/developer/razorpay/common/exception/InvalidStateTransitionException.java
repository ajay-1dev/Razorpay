package com.ajay.developer.razorpay.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {
    private String currentState;
    private String event;
    public InvalidStateTransitionException(String currentState, String event) {
        super("Invalid state transition. Current state: " + currentState + ", event: " + event);
        this.currentState = currentState;
        this.event = event;
    }
}
