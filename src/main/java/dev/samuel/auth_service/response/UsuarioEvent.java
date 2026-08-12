package dev.samuel.auth_service.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record UsuarioEvent(
        String nome,
        String email
) implements Serializable {}
