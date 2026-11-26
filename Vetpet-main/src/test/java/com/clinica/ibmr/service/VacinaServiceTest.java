package com.clinica.ibmr.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.clinica.ibmr.api.ApiDtos.VacinaRequest;
import com.clinica.ibmr.api.ApiExceptions.InvalidRequestException;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.repository.*;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class VacinaServiceTest {
    private AnimalService animais;
    private VeterinarioService veterinarios;
    private VacinaRepository vacinas;
    private IntegracaoGovernoRepository integracoes;
    private VinculoMicrochipRepository microchips;
    private ApplicationEventPublisher eventos;
    private VacinaService service;
    private Animal animal;
    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        animais = mock(AnimalService.class);
        veterinarios = mock(VeterinarioService.class);
        vacinas = mock(VacinaRepository.class);
        integracoes = mock(IntegracaoGovernoRepository.class);
        microchips = mock(VinculoMicrochipRepository.class);
        eventos = mock(ApplicationEventPublisher.class);
        service = new VacinaService(animais, veterinarios, vacinas, integracoes, microchips, eventos);
        animal = mock(Animal.class);
        veterinario = mock(Veterinario.class);
        when(animal.getId()).thenReturn(9L);
        when(veterinario.getId()).thenReturn(4L);
        when(animais.obterEntidadeAtivo(9L)).thenReturn(animal);
        when(veterinarios.obterAtivo("vet@clinica.test")).thenReturn(veterinario);
        when(vacinas.save(any(Vacina.class))).thenAnswer(invocation -> {
            Vacina vacina = invocation.getArgument(0);
            ReflectionTestUtils.setField(vacina, "id", 31L);
            return vacina;
        });
        when(integracoes.existsByVacina_Id(31L)).thenReturn(false);
        when(microchips.findByAnimal_IdAndDataDesvinculacaoIsNull(9L)).thenReturn(Optional.empty());
    }

    @Test
    void appliedRabiesVaccineCreatesOnlyPendingIntegrationRecord() {
        var result = service.registrar(request(ClinicaEnums.TipoVacina.ANTIRRABICA,
                ClinicaEnums.SituacaoVacina.APLICADA, LocalDate.now()), "vet@clinica.test");

        assertTrue(result.antirrabica());
        var captor = org.mockito.ArgumentCaptor.forClass(IntegracaoGoverno.class);
        verify(integracoes).save(captor.capture());
        IntegracaoGoverno integration = captor.getValue();
        assertEquals(ClinicaEnums.SituacaoIntegracao.PENDENTE, integration.getSituacao());
        assertEquals(ClinicaEnums.TipoOperacaoIntegracao.VACINA_ANTIRRABICA, integration.getTipoOperacao());
        assertNull(integration.getProtocoloExterno());
        assertEquals("vacina-antirrabica-31", integration.getChaveIdempotencia());
        verify(eventos).publishEvent(argThat((Object event) ->
                event instanceof com.clinica.ibmr.integration.VacinaAntirrabicaAplicadaEvent applied
                        && applied.vacinaId().equals(31L)));
    }

    @Test
    void nonRabiesVaccineDoesNotCreateGovernmentIntegration() {
        service.registrar(request(ClinicaEnums.TipoVacina.POLIVALENTE,
                ClinicaEnums.SituacaoVacina.APLICADA, LocalDate.now()), "vet@clinica.test");
        verifyNoInteractions(integracoes);
    }

    @Test
    void scheduledRabiesVaccineDoesNotQueueBeforeApplication() {
        service.registrar(request(ClinicaEnums.TipoVacina.ANTIRRABICA,
                ClinicaEnums.SituacaoVacina.AGENDADA, null), "vet@clinica.test");
        verifyNoInteractions(integracoes);
    }

    @Test
    void appliedVaccineRequiresApplicationDate() {
        assertThrows(InvalidRequestException.class, () -> service.registrar(
                request(ClinicaEnums.TipoVacina.ANTIRRABICA, ClinicaEnums.SituacaoVacina.APLICADA, null),
                "vet@clinica.test"));
        verify(vacinas, never()).save(any());
    }

    private VacinaRequest request(ClinicaEnums.TipoVacina tipo, ClinicaEnums.SituacaoVacina situacao,
                                  LocalDate dataAplicacao) {
        return new VacinaRequest(9L, "Vacina", tipo, "Fabricante", "LT-1", dataAplicacao,
                "Dose 1", "Subcutânea", dataAplicacao == null ? null : dataAplicacao.plusYears(1),
                LocalDate.now().plusYears(2), null, situacao);
    }
}
