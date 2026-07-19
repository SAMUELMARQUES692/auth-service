package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScopeService {

    private final ScopeRepository scopeRepository;

    public Scope findById(Long id) {
        return scopeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scope não encotrado com o ID: " + id));
    }

}
