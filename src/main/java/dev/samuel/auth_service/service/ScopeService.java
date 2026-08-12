package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.exception.ScopeNotFoundException;
import dev.samuel.auth_service.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScopeService {

    private final ScopeRepository scopeRepository;

    public Scope findByNome(String nome) {
        return scopeRepository.findByNome(nome)
                .orElseThrow(() -> new ScopeNotFoundException("Scope não encontrado com o nome: " + nome));
    }

}
