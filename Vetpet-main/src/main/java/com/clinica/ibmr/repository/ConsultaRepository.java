package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.Consulta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findAllByAnimal_IdOrderByDataHoraDesc(Long animalId);
}
