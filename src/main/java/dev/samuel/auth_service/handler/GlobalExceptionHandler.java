package dev.samuel.auth_service.handler;

import dev.samuel.auth_service.exception.EmailJaCadastradoException;
import dev.samuel.auth_service.exception.EmailNotFoundException;
import dev.samuel.auth_service.exception.UserOrPasswordIncorrectException;
import dev.samuel.auth_service.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserOrPasswordIncorrectException.class)
    public ResponseEntity<ErrorResponse> userAndPasswordIncorrect(UserOrPasswordIncorrectException exception) {
        ErrorResponse error = new ErrorResponse(
                "CREDENCIAIS_INCORRETAS",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }


    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponse> emailDuplicado(EmailJaCadastradoException exception) {
        ErrorResponse error = new ErrorResponse(
                "EMAIL_JA_CADASTRADO",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> emailDuplicado(EmailNotFoundException exception) {
        ErrorResponse error = new ErrorResponse(
                "EMAIL_NAO_ENCONTRADO",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    // Fallback para erros inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error: ", ex); // adicione isso
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
