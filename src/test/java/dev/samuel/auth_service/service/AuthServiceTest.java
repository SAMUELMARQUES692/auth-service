package dev.samuel.auth_service.service;

import dev.samuel.auth_service.configuration.TokenService;
import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.exception.UserOrPasswordIncorrectException;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.AuthRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    TokenService tokenService;

    @Test
    void login() {
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

        AuthRequest request = AuthRequest.builder()
                .email("Email Teste")
                .senha("Senha Teste")
                .build();

        Mockito.when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuario));
        Mockito.when(passwordEncoder.matches(request.senha(), usuario.getSenha())).thenReturn(true);
        Mockito.when(tokenService.gerarToken(usuario)).thenReturn("token");

        authService.login(request);

        Mockito.verify(usuarioRepository).findByEmail(request.email());
        Mockito.verify(passwordEncoder).matches(request.senha(), usuario.getSenha());
        Mockito.verify(tokenService).gerarToken(usuario);
    }

    @Test
    void login_senhaIncorreta_deveLancarExcecao() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("Email Teste")
                .senha("Senha Teste")
                .build();

        AuthRequest request = AuthRequest.builder()
                .email("Email Teste")
                .senha("SenhaErrada")
                .build();

        Mockito.when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuario));
        Mockito.when(passwordEncoder.matches(request.senha(), usuario.getSenha())).thenReturn(false);

        assertThrows(UserOrPasswordIncorrectException.class, () -> authService.login(request));

        Mockito.verify(tokenService, Mockito.never()).gerarToken(Mockito.any());
    }

    @Test
    void login_emailNaoEncontrado_deveLancarExcecao() {
        AuthRequest request = AuthRequest.builder()
                .email("naoexiste@teste.com")
                .senha("qualquerSenha")
                .build();

        Mockito.when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UserOrPasswordIncorrectException.class, () -> authService.login(request));

        Mockito.verify(tokenService, Mockito.never()).gerarToken(Mockito.any());
    }
}