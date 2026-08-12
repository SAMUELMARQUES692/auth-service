package dev.samuel.auth_service.repository;

import dev.samuel.auth_service.entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScopeRepository extends JpaRepository<Scope, Long> {

    Optional<Scope> findByNome(String nome);
}
