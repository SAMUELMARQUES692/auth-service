package dev.samuel.auth_service.response;

import java.io.Serializable;

public record UsuarioCadastradoEvent(
        String nome,
        String email
) implements Serializable {}
