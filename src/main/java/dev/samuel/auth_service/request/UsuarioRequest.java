package dev.samuel.auth_service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioRequest(

        @NotBlank
        String nome,

        @NotBlank
        String email,

        @NotBlank
        String senha,

        @NotEmpty
        @NotNull
        List<Long> scopes

) {}
