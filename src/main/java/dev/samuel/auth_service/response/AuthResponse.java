package dev.samuel.auth_service.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {}
