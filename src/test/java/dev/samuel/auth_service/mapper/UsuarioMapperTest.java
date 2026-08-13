package dev.samuel.auth_service.mapper;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    void toEntity() {
        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(1L))
                .build();

        Usuario usuario = mapper.toEntity(request);

        assertNotNull(usuario);

        assertEquals(request.nome(), usuario.getNome());
        assertEquals(request.email(), usuario.getEmail());
        assertEquals(request.senha(), usuario.getSenha());
        assertTrue(usuario.getScopes() == null || usuario.getScopes().isEmpty());
    }

    @Test
    void toUsuarioResponse() {
        Scope scope = Scope.builder()
                .id(1L)
                .nome("nome Test")
                .build();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(scope))
                .createdAt(LocalDateTime.now())
                .build();

        UsuarioResponse response = mapper.toUsuarioResponse(usuario);

        assertNotNull(response);

        assertEquals(usuario.getId(), response.id());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getCreatedAt(), response.createdAt());
    }

    @Test
    void atualizarUsuario() {
        Scope scope = Scope.builder()
                .id(1L)
                .nome("nome Test")
                .build();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .createdAt(LocalDateTime.now())
                .build();

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(1L))
                .build();

        mapper.atualizarUsuario(request, usuario);

        assertNotNull(request);

        assertEquals(request.nome(), usuario.getNome());
        assertEquals(request.email(), usuario.getEmail());
        assertEquals(request.senha(), usuario.getSenha());
        assertTrue(usuario.getScopes() == null || usuario.getScopes().isEmpty());
    }

    @Test
    void mapScopeEntitiesToStringScopes() {
        // Cria o Scopes
        Scope scope1 = Scope.builder().id(1L).nome("ADMIN").build();
        Scope scope2 = Scope.builder().id(2L).nome("USER").build();
        // Aninha os scopes criados dentro de uma lista
        List<Scope> scopes = List.of(scope1, scope2);

        // Coloca o metodo dentro de uma variavel de controle que recebe uma lista de Strings.
        List<String> resultado = mapper.mapScopeEntitiesToStringScopes(scopes);

        // Assert - confirma que os nomes foram extraídos corretamente
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains("ADMIN"));
        assertTrue(resultado.contains("USER"));
    }

    @Test
    void mapScopeEntitiesToStringScopesScopesNull() {
        List<String> resultado = mapper.mapScopeEntitiesToStringScopes(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void mapScopeEntitiesToStringScopesListaVazia() {
        List<String> resultado = mapper.mapScopeEntitiesToStringScopes(List.of());

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}