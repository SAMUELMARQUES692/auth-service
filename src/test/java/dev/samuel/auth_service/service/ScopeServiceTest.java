package dev.samuel.auth_service.service;

import dev.samuel.auth_service.entity.Scope;
import dev.samuel.auth_service.repository.ScopeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScopeServiceTest {

    @InjectMocks
    ScopeService scopeService;

    @Mock
    ScopeRepository scopeRepository;

    @Test
    void findById() {
        Scope scope = Scope.builder()
                .id(1L)
                .nome("nome Test")
                .build();

        Mockito.when(scopeRepository.findById(scope.getId())).thenReturn(Optional.of(scope));

        scopeService.findById(scope.getId());

        Mockito.verify(scopeRepository).findById(scope.getId());
    }
}