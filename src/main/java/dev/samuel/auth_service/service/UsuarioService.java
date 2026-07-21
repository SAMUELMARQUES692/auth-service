package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.exception.EmailJaCadastradoException;
import dev.samuel.auth_service.exception.EmailNotFoundException;
import dev.samuel.auth_service.mapper.UsuarioMapper;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.UsuarioRequest;
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

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("Este email já esta em uso");
        }

        List<Scope> scopes = request.scopes().stream()
                .map(scopeService::findById)
                .toList();

        Usuario newUsuario = usuarioMapper.toEntity(request);
        newUsuario.setScopes(scopes);
        newUsuario.setSenha(passwordEncoder.encode(request.senha()));
        Usuario salvar = usuarioRepository.save(newUsuario);
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
