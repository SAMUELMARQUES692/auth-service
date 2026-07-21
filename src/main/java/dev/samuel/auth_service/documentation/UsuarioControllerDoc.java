package dev.samuel.auth_service.documentation;

import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Usuarios", description = "Recurso responsavel pelo gerenciamento de usuarios da API")
public interface UsuarioControllerDoc {

    @Operation(summary = "Salvar Usuario", description = "Metodo responsavel por cadastrar e salvar novos usuarios no banco de dados")
    @ApiResponse(responseCode = "201", description = "Usuario salvo com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    ResponseEntity<UsuarioResponse> cadastrar(@RequestBody UsuarioRequest request);

    @Operation(summary = "Atualizar Usuario", description = "Metodo responsavel por atualizar usuarios no banco de dados",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Usuario não encontrado", content = @Content())
    ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequest request);

    @Operation(summary = "Busca os Usuarios pelo email", description = "Metodo responsavel por buscar usuarios usando o email",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Usuario encontrado com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Usuario não encontrado", content = @Content())
    ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email);

    @Operation(summary = "Busca os Usuarios pelo ID", description = "Metodo responsavel por buscar usuarios usando o ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Usuario encontrado com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Usuario não encontrado", content = @Content())
    ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id);

    @Operation(summary = "Deleta Usuarios pelo ID", description = "Metodo responsavel por deletar usuarios pelo ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso", content = @Content())
    @ApiResponse(responseCode = "404", description = "Usuario não encontrado", content = @Content())
    ResponseEntity<Void> deletar(@PathVariable Long id);
}
