package dev.samuel.auth_service.documentation;

import dev.samuel.auth_service.request.AuthRequest;
import dev.samuel.auth_service.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação", description = "Recurso responsavel pela autenticação de usuarios para as requisições da API sejam feitas")
public interface AuthControllerDoc {

    @Operation(summary = "Login Usuario", description = "Metodo responsavel pelo login e geração de token")
    @ApiResponse(responseCode = "200", description = "Login executado com sucesso", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Usuario ou senha invalidos", content = @Content())
    ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request);

}
