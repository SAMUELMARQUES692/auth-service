package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.entity.Usuario;
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
            throw new RuntimeException("Este email já esta em uso");
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


}
