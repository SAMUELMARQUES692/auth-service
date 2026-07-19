package dev.samuel.auth_service.documentation;

import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Usuarios", description = "Recurso responsavel pelo gerenciamento de usuarios da API")
public interface UsuarioControllerDoc {

    @Operation(summary = "Salvar Usuario", description = "Metodo responsavel por cadastrar e salvar novos usuarios no banco de dados")
    @ApiResponse(responseCode = "201", description = "Usuario salvo com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    ResponseEntity<UsuarioResponse> cadastrar(@RequestBody UsuarioRequest request);
}
