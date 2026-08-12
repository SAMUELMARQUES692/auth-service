package dev.samuel.auth_service.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        LocalDateTime createdAt,
        List<String> scopes
) {}
