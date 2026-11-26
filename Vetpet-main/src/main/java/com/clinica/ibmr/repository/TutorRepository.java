package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.Tutor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByPessoa_Cpf(String cpf);
}
