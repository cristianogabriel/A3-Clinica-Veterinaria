package com.api.governo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.api.governo.dto.VacinacaoRequest;
import com.api.governo.entity.AnimalGov;
import com.api.governo.entity.OperacaoGov;
import com.api.governo.entity.VacinacaoGov;
import com.api.governo.repository.AnimalGovRepository;
import com.api.governo.repository.OperacaoGovRepository;
import com.api.governo.repository.VacinacaoGovRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

class GovernoServiceTest {
    @Test
    void persistsOperationBeforeVaccinationAndReturnsGeneratedIds() {
        AnimalGovRepository animais = org.mockito.Mockito.mock(AnimalGovRepository.class);
        OperacaoGovRepository operacoes = org.mockito.Mockito.mock(OperacaoGovRepository.class);
        VacinacaoGovRepository vacinacoes = org.mockito.Mockito.mock(VacinacaoGovRepository.class);
        var service = new GovernoService(animais, operacoes, vacinacoes, new ObjectMapper().findAndRegisterModules());

        when(operacoes.findByChaveIdempotencia("idempotencia-1")).thenReturn(Optional.empty());
        when(animais.findByCodigoMicrochip("985112345678901")).thenReturn(Optional.empty());
        when(animais.save(any())).thenAnswer(invocation -> {
            AnimalGov animal = invocation.getArgument(0);
            ReflectionTestUtils.setField(animal, "id", 10L);
            return animal;
        });
        when(operacoes.saveAndFlush(any())).thenAnswer(invocation -> {
            OperacaoGov operacao = invocation.getArgument(0);
            ReflectionTestUtils.setField(operacao, "id", 20L);
            return operacao;
        });
        when(vacinacoes.saveAndFlush(any())).thenAnswer(invocation -> {
            VacinacaoGov vacinacao = invocation.getArgument(0);
            ReflectionTestUtils.setField(vacinacao, "id", 30L);
            return vacinacao;
        });

        var request = new VacinacaoRequest("985112345678901", "Rex", "Cão", "Preto", "M",
                "SRD", LocalDate.of(2022, 1, 1), "Antirrábica", "Fabricante", "LT-1",
                LocalDate.of(2026, 1, 15), "Dose anual", "Subcutânea", null, null,
                "90001", "RJ", "Veterinária");

        var response = service.vacinar(request, "idempotencia-1");

        ArgumentCaptor<OperacaoGov> operacaoCaptor = ArgumentCaptor.forClass(OperacaoGov.class);
        ArgumentCaptor<VacinacaoGov> vacinacaoCaptor = ArgumentCaptor.forClass(VacinacaoGov.class);
        InOrder ordem = inOrder(operacoes, vacinacoes);
        ordem.verify(operacoes).saveAndFlush(operacaoCaptor.capture());
        ordem.verify(vacinacoes).saveAndFlush(vacinacaoCaptor.capture());
        ordem.verify(operacoes).save(operacaoCaptor.getValue());

        assertEquals(20L, operacaoCaptor.getValue().getId());
        assertEquals(30L, response.get("idVacinacao"));
        assertEquals(operacaoCaptor.getValue().getProtocolo(), response.get("protocolo"));
    }
}
