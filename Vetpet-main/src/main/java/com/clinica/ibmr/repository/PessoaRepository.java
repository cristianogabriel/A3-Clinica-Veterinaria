package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.Pessoa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmailIgnoreCase(String email);
    Optional<Pessoa> findByEmailIgnoreCase(String email);
}
