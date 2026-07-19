package dev.samuel.auth_service.service;

import dev.samuel.auth_service.configuration.TokenService;
import dev.samuel.auth_service.entity.Usuario;
import dev.samuel.auth_service.exception.UserOrPasswordIncorrectException;
import dev.samuel.auth_service.repository.UsuarioRepository;
import dev.samuel.auth_service.request.AuthRequest;
import dev.samuel.auth_service.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private static final long EXPIRES_IN_SECONDS = 3600L;

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(request.senha(), u.getSenha()))
                .orElseThrow(() -> new UserOrPasswordIncorrectException("Email ou senha inválidos"));

        String token = tokenService.gerarToken(usuario);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(EXPIRES_IN_SECONDS)
                .build();
    }


    private boolean isPasswordCorrect(String password, String savePassword) {
        return passwordEncoder.matches(password, savePassword);
    }



}
