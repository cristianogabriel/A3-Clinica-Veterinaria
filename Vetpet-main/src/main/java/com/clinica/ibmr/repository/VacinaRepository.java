package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.domain.Vacina;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Collection;

public interface VacinaRepository extends JpaRepository<Vacina, Long> {
    Optional<Vacina> findByIdAndAnimal_Id(Long id, Long animalId);
    List<Vacina> findAllByAnimal_IdOrderByDataAplicacaoDesc(Long animalId);
    List<Vacina> findAllBySituacaoAndDataProximaDoseGreaterThanEqualOrderByDataProximaDoseAsc(
            ClinicaEnums.SituacaoVacina situacao, LocalDate data);
    List<Vacina> findAllBySituacaoInAndDataProximaDoseGreaterThanEqualOrderByDataProximaDoseAsc(
            Collection<ClinicaEnums.SituacaoVacina> situacoes, LocalDate data);
    List<Vacina> findAllByAnimal_IdAndSituacaoAndDataProximaDoseGreaterThanEqualOrderByDataProximaDoseAsc(
            Long animalId, ClinicaEnums.SituacaoVacina situacao, LocalDate data);
}
