package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.Raca;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacaRepository extends JpaRepository<Raca, Long> {
    List<Raca> findAllByEspecie_IdOrderByNomeAsc(Long especieId);
}
