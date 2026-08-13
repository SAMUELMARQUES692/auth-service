package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.exception.EmailJaCadastradoException;
import dev.samuel.auth_service.exception.EmailNotFoundException;
import dev.samuel.auth_service.mapper.UsuarioMapper;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    ScopeService scopeService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    UsuarioMapper usuarioMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    EventPublisher eventPublisher;

    @Captor
    ArgumentCaptor<Usuario> argumentCaptor;

    @Test
    void cadastrar() {
        Scope scope = Scope.builder()
                .id(1L)
                .nome("USER")
                .build();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(scope))
                .createdAt(LocalDateTime.now())
                .build();

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(scope.getId()))
                .build();

        Mockito.when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        Mockito.when(scopeService.findByNome(scope.getNome())).thenReturn(scope);
        Mockito.when(usuarioMapper.toEntity(request)).thenReturn(usuario);
        Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);
        Mockito.when(passwordEncoder.encode(request.senha())).thenReturn("senhaCriptografada");

        usuarioService.cadastrar(request);

        Mockito.verify(usuarioRepository).existsByEmail(request.email());
        Mockito.verify(scopeService).findByNome(scope.getNome());
        Mockito.verify(usuarioMapper).toEntity(request);
        Mockito.verify(usuarioRepository).save(argumentCaptor.capture());
        Mockito.verify(eventPublisher).publicarUsuarioCadastrado(Mockito.any());
        Mockito.verify(usuarioMapper).toUsuarioResponse(usuario);
    }

    @Test
    void buscarTodos() {
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

        Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        usuarioService.buscarTodos();

        Mockito.verify(usuarioRepository).findAll();
        Mockito.verify(usuarioMapper).toUsuarioResponse(Mockito.any());
    }

    @Test
    void atualizar() {
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

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(1L))
                .build();

        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.atualizar(usuario.getId(), request);

        Mockito.verify(usuarioRepository).findById(usuario.getId());
        Mockito.verify(usuarioMapper).atualizarUsuario(request, usuario);
        Mockito.verify(usuarioRepository).save(argumentCaptor.capture());
        Mockito.verify(eventPublisher).publicarUsuarioAtualizado(Mockito.any());
        Mockito.verify(usuarioMapper).toUsuarioResponse(Mockito.any());
    }

    @Test
    void atualizarUsuarioNaoEncontradoDeveLancarExcecao() {
        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(1L))
                .build();

        Mockito.when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmailNotFoundException.class, () -> usuarioService.atualizar(999L, request));

        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void buscarPorEmail() {
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

        Mockito.when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        usuarioService.buscarPorEmail(usuario.getEmail());

        Mockito.verify(usuarioRepository).findByEmail(usuario.getEmail());
        Mockito.verify(usuarioMapper).toUsuarioResponse(Mockito.any());
    }

    @Test
    void buscarPorId() {
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

        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        usuarioService.buscarPorId(usuario.getId());

        Mockito.verify(usuarioRepository).findById(usuario.getId());
        Mockito.verify(usuarioMapper).toUsuarioResponse(Mockito.any());
    }

    @Test
    void buscarPorIdUsuarioNaoEncontradoDeveLancarExcecao() {
        Mockito.when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmailNotFoundException.class, () -> usuarioService.buscarPorId(999L));
    }


    @Test
    void deletar() {
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

        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        usuarioService.deletar(usuario.getId());

        Mockito.verify(usuarioRepository).findById(usuario.getId());
        Mockito.verify(usuarioRepository).deleteById(usuario.getId());
    }

    @Test
    void deletarUsuarioNaoEncontradoDeveLancarExcecao() {
        Mockito.when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmailNotFoundException.class, () -> usuarioService.deletar(999L));

        Mockito.verify(usuarioRepository, Mockito.never()).deleteById(Mockito.any());
    }


    @Test // este teste serve para testar as exceptions dos metodos da service, caso o email ja exista no banco de dados, ele deve lançar a exception EmailJaCadastradoException
    void deveRetornarUmaExceptionQuantoOEmailJaExistir() {

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .senha("Senha Teste")
                .scopes(List.of(1L))
                .build();

        Mockito.when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        EmailJaCadastradoException exception = assertThrows(EmailJaCadastradoException.class,
                () -> {
           usuarioService.cadastrar(request);
        });

        Assertions.assertEquals(exception.getMessage(), "Este email já esta em uso");
    }
}