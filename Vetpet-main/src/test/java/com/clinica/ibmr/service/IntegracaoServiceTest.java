package com.clinica.ibmr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.clinica.ibmr.api.ApiExceptions.ExternalServiceException;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.integration.GovernoIntegrationClient;
import com.clinica.ibmr.repository.AnimalRepository;
import com.clinica.ibmr.repository.IntegracaoGovernoRepository;
import com.clinica.ibmr.repository.VinculoMicrochipRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegracaoServiceTest {
    private IntegracaoGovernoRepository integracoes;
    private VinculoMicrochipRepository microchips;
    private GovernoIntegrationClient governo;
    private IntegracaoGoverno integracao;
    private IntegracaoService service;

    @BeforeEach
    void setUp() {
        integracoes = mock(IntegracaoGovernoRepository.class);
        microchips = mock(VinculoMicrochipRepository.class);
        governo = mock(GovernoIntegrationClient.class);
        service = new IntegracaoService(integracoes, mock(AnimalRepository.class), microchips, governo);

        integracao = mock(IntegracaoGoverno.class);
        Animal animal = mock(Animal.class);
        Vacina vacina = mock(Vacina.class);
        Veterinario veterinario = mock(Veterinario.class);
        Pessoa pessoa = mock(Pessoa.class);
        VinculoMicrochip vinculo = mock(VinculoMicrochip.class);
        when(integracao.getId()).thenReturn(6L);
        when(integracao.getAnimal()).thenReturn(animal);
        when(integracao.getVacina()).thenReturn(vacina);
        when(integracao.getChaveIdempotencia()).thenReturn("vacina-antirrabica-31");
        when(integracao.getSituacao()).thenReturn(ClinicaEnums.SituacaoIntegracao.PENDENTE,
                ClinicaEnums.SituacaoIntegracao.FALHA);
        when(animal.getId()).thenReturn(9L);
        when(animal.getNome()).thenReturn("Rex");
        when(vacina.getId()).thenReturn(31L);
        when(vacina.getNome()).thenReturn("Antirrábica");
        when(vacina.getDataAplicacao()).thenReturn(LocalDate.of(2026, 1, 15));
        when(vacina.getFabricante()).thenReturn("Fabricante");
        when(vacina.getNumeroLote()).thenReturn("LT-1");
        when(vacina.getVeterinario()).thenReturn(veterinario);
        when(veterinario.getId()).thenReturn(4L);
        when(veterinario.getPessoa()).thenReturn(pessoa);
        when(veterinario.getNumeroCrmv()).thenReturn("90001");
        when(veterinario.getUfCrmv()).thenReturn("RJ");
        when(pessoa.getNomeCompleto()).thenReturn("Veterinária");
        when(vinculo.getIdentificador()).thenReturn("985112345678901");
        when(microchips.findByAnimal_IdAndDataDesvinculacaoIsNull(9L)).thenReturn(Optional.of(vinculo));
        when(integracoes.findById(6L)).thenReturn(Optional.of(integracao));
        when(governo.enviarVacinaAntirrabica(any()))
                .thenThrow(new ExternalServiceException("Serviço indisponível."))
                .thenReturn(new GovernoIntegrationClient.ResultadoEnvio("PROTOCOLO-6", "Recebida"));
    }

    @Test
    void failedSendCanBeRetriedWithoutChangingIdempotencyKey() {
        service.tentarNovamente(6L);
        service.tentarNovamente(6L);

        var solicitacoes = org.mockito.ArgumentCaptor.forClass(
                GovernoIntegrationClient.SolicitacaoVacinaAntirrabica.class);
        verify(governo, times(2)).enviarVacinaAntirrabica(solicitacoes.capture());
        assertEquals("vacina-antirrabica-31", solicitacoes.getAllValues().get(0).idempotencyKey());
        assertEquals(solicitacoes.getAllValues().get(0).idempotencyKey(),
                solicitacoes.getAllValues().get(1).idempotencyKey());
        verify(integracao, times(2)).iniciarTentativa();
        verify(integracao).falhar(eq("Serviço indisponível."), any());
        verify(integracao).confirmar(eq("PROTOCOLO-6"), any());
    }
}
