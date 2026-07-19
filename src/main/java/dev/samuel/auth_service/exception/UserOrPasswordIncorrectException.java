package dev.samuel.auth_service.exception;

public class UserOrPasswordIncorrectException extends RuntimeException{
    public UserOrPasswordIncorrectException(String messge) {
        super(messge);
    }
}
