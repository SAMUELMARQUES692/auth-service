package dev.samuel.auth_service.response;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        LocalDateTime createdAt,
        List<String> scopes
) {}
