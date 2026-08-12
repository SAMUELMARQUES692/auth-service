package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.exception.EmailJaCadastradoException;
import dev.samuel.auth_service.exception.EmailNotFoundException;
import dev.samuel.auth_service.mapper.UsuarioMapper;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.UsuarioRequest;
import dev.samuel.auth_service.response.UsuarioEvent;
import dev.samuel.auth_service.response.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final ScopeService scopeService;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("Este email já esta em uso");
        }

        Scope scopeUser = scopeService.findByNome("USER");

        Usuario newUsuario = usuarioMapper.toEntity(request);
        newUsuario.setScopes(List.of(scopeUser));
        newUsuario.setSenha(passwordEncoder.encode(request.senha()));
        Usuario salvar = usuarioRepository.save(newUsuario);
        eventPublisher.publicarUsuarioCadastrado(new UsuarioEvent(salvar.getNome(), salvar.getEmail()));
        return usuarioMapper.toUsuarioResponse(salvar);
    }

    public List<UsuarioResponse> buscarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toUsuarioResponse)
                .toList();
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EmailNotFoundException("Email não encontrado com o ID: " + id));


        usuarioMapper.atualizarUsuario(request, usuario);
        Usuario salvo = usuarioRepository.save(usuario);
        eventPublisher.publicarUsuarioAtualizado(new UsuarioEvent(salvo.getNome(), salvo.getEmail()));
        return usuarioMapper.toUsuarioResponse(salvo);
    }

    public UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email não encontrado"));
        return usuarioMapper.toUsuarioResponse(usuario);
    }

    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EmailNotFoundException("Email não encontrado com o ID: " + id));
        return usuarioMapper.toUsuarioResponse(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new EmailNotFoundException("Email não encontrado com o ID: " + id));
        usuarioRepository.deleteById(id);
    }


}
