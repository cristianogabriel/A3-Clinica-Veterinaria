package com.clinica.ibmr.repository;

import com.clinica.ibmr.domain.Animal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findAllByTutor_IdOrderByNomeAsc(Long tutorId);
}
