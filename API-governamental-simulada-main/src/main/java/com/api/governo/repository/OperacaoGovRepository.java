package com.api.governo.repository;
import com.api.governo.entity.OperacaoGov;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OperacaoGovRepository extends JpaRepository<OperacaoGov, Long> {
    Optional<OperacaoGov> findByChaveIdempotencia(String chave);
    Optional<OperacaoGov> findByProtocolo(String protocolo);
}
