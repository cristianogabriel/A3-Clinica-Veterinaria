package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.VinculoMicrochip;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VinculoMicrochipRepository extends JpaRepository<VinculoMicrochip, Long> {
    boolean existsByIdentificador(String identificador);
    Optional<VinculoMicrochip> findByAnimal_IdAndDataDesvinculacaoIsNull(Long animalId);
    List<VinculoMicrochip> findAllByAnimal_IdOrderByDataVinculacaoDesc(Long animalId);
}
