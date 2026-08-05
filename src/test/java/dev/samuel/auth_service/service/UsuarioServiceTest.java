package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.exception.EmailJaCadastradoException;
import dev.samuel.auth_service.mapper.UsuarioMapper;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.UsuarioRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    UsuarioMapper usuarioMapper;

    @Test
    void cadastrar() {
    }


    @Test
    void buscarTodos() {
    }

    @Test
    void atualizar() {
    }

    @Test
    void buscarPorEmail() {
    }

    @Test
    void buscarPorId() {
    }

    @Test
    void deletar() {
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