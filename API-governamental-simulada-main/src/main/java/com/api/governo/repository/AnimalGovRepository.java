package com.api.governo.repository;
import com.api.governo.entity.AnimalGov;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AnimalGovRepository extends JpaRepository<AnimalGov, Long> {
    Optional<AnimalGov> findByCodigoMicrochip(String codigoMicrochip);
    boolean existsByCodigoMicrochip(String codigoMicrochip);
}
