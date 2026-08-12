package dev.samuel.auth_service.exception;

public class ScopeNotFoundException extends RuntimeException{
    public ScopeNotFoundException(String message) {
        super(message);
    }
}
