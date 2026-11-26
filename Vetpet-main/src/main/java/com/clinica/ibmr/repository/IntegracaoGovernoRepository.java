package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.IntegracaoGoverno;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegracaoGovernoRepository extends JpaRepository<IntegracaoGoverno, Long> {
    List<IntegracaoGoverno> findAllByAnimal_IdOrderByDataHoraSolicitacaoDesc(Long animalId);
    Optional<IntegracaoGoverno> findByVacina_Id(Long vacinaId);
    boolean existsByVacina_Id(Long vacinaId);
}
