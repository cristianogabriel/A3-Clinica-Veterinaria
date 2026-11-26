package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.Veterinario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Optional<Veterinario> findByPessoa_EmailIgnoreCase(String email);
    boolean existsByNumeroCrmvAndUfCrmv(String numeroCrmv, String ufCrmv);
}
