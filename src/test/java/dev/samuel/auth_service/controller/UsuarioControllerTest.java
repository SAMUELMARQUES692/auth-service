package dev.samuel.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.samuel.auth_service.configuration.BaseIntegrationTest;
import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.repository.ScopeRepository;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerTest extends BaseIntegrationTest {

    @Autowired
    ScopeRepository scopeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void cadastrar() throws Exception {
        scopeRepository.save(Scope.builder().nome("ADMIN").build());
        Scope scopeUser = scopeRepository.save(Scope.builder().nome("USER").build());

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(1L))
                .build();

        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(request.nome()))
                .andExpect(jsonPath("$.email").value(request.email()));

        Usuario usuarioSalvo = usuarioRepository.findByEmail(request.email()).orElseThrow();
        assertEquals(1, usuarioSalvo.getScopes().size());
        assertEquals("USER", usuarioSalvo.getScopes().get(0).getNome());
    }

    @Test
    void buscarTodos() throws Exception {
        Scope scope = scopeRepository.save(
                Scope.builder()
                .nome("nome Test")
                .build()
        );

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha(passwordEncoder.encode("Senha Teste"))
                .scopes(List.of(scope))
                .createdAt(LocalDateTime.now())
                .build()
        );

        UsuarioResponse response = UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .createdAt(usuario.getCreatedAt())
                .build();

        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.id()))
                .andExpect(jsonPath("$[0].nome").value(response.nome()))
                .andExpect(jsonPath("$[0].email").value(response.email()));
    }

    @Test
    void atualizar() throws Exception{
        Scope scope = scopeRepository.save(
                Scope.builder()
                        .nome("nome Test")
                        .build()
        );

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .nome("Nome Teste")
                        .email("Email Teste")
                        .senha(passwordEncoder.encode("Senha Teste"))
                        .scopes(List.of(scope))
                        .createdAt(LocalDateTime.now())
                        .build()
        );


        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(scope.getId()))
                .build();

        mockMvc.perform(put("/api/usuarios/{id}", usuario.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value(request.nome()))
                .andExpect(jsonPath("$.email").value(request.email()));
    }

    @Test
    void buscarPorEmail() throws Exception{
        Scope scope = scopeRepository.save(
                Scope.builder()
                        .nome("nome Test")
                        .build()
        );

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .nome("Nome Teste")
                        .email("Email Teste")
                        .senha(passwordEncoder.encode("Senha Teste"))
                        .scopes(List.of(scope))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        mockMvc.perform(get("/api/usuarios/buscar-email")
                        .param("email", usuario.getEmail())
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    void buscarPorId() throws Exception{
        Scope scope = scopeRepository.save(
                Scope.builder()
                        .nome("nome Test")
                        .build()
        );

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .nome("Nome Teste")
                        .email("Email Teste")
                        .senha(passwordEncoder.encode("Senha Teste"))
                        .scopes(List.of(scope))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        mockMvc.perform(get("/api/usuarios/{id}", usuario.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    void deletar() throws Exception {
        Scope scope = scopeRepository.save(
                Scope.builder()
                        .nome("nome Test")
                        .build()
        );

        Usuario usuario = usuarioRepository.save(
                Usuario.builder()
                        .nome("Nome Teste")
                        .email("Email Teste")
                        .senha(passwordEncoder.encode("Senha Teste"))
                        .scopes(List.of(scope))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        mockMvc.perform(delete("/api/usuarios/{id}", usuario.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNoContent());
    }
}