package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.IntegracaoResponse;
import com.clinica.ibmr.api.ApiExceptions.ExternalServiceException;
import com.clinica.ibmr.api.ApiExceptions.InvalidRequestException;
import com.clinica.ibmr.api.ApiExceptions.NotFoundException;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.domain.IntegracaoGoverno;
import com.clinica.ibmr.integration.GovernoIntegrationClient;
import com.clinica.ibmr.integration.GovernoIntegrationClient.SolicitacaoVacinaAntirrabica;
import com.clinica.ibmr.repository.AnimalRepository;
import com.clinica.ibmr.repository.IntegracaoGovernoRepository;
import com.clinica.ibmr.repository.VinculoMicrochipRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegracaoService {
    private static final Logger log = LoggerFactory.getLogger(IntegracaoService.class);
    private final IntegracaoGovernoRepository integracoes;
    private final AnimalRepository animais;
    private final VinculoMicrochipRepository microchips;
    private final GovernoIntegrationClient governo;
    public IntegracaoService(IntegracaoGovernoRepository integracoes, AnimalRepository animais,
                             VinculoMicrochipRepository microchips, GovernoIntegrationClient governo) {
        this.integracoes = integracoes; this.animais = animais;
        this.microchips = microchips; this.governo = governo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IntegracaoResponse processarPorVacina(Long vacinaId) {
        IntegracaoGoverno integracao = integracoes.findByVacina_Id(vacinaId)
                .orElseThrow(() -> new NotFoundException("Operação de integração não encontrada."));
        return processar(integracao);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IntegracaoResponse tentarNovamente(Long integracaoId) {
        IntegracaoGoverno integracao = integracoes.findById(integracaoId)
                .orElseThrow(() -> new NotFoundException("Operação de integração não encontrada."));
        return processar(integracao);
    }

    private IntegracaoResponse processar(IntegracaoGoverno integracao) {
        if (integracao.getSituacao() == ClinicaEnums.SituacaoIntegracao.CONFIRMADA) {
            return ApiMapper.integracao(integracao);
        }
        if (integracao.getSituacao() != ClinicaEnums.SituacaoIntegracao.PENDENTE
                && integracao.getSituacao() != ClinicaEnums.SituacaoIntegracao.FALHA) {
            throw new InvalidRequestException("A integração não está disponível para envio ou retentativa.");
        }

        integracao.iniciarTentativa();
        try {
            var vacina = integracao.getVacina();
            if (vacina == null) {
                throw new ExternalServiceException("A integração não possui uma aplicação de vacina associada.");
            }
            var animal = integracao.getAnimal();
            var vinculo = integracao.getVinculoMicrochip();
            if (vinculo == null) {
                vinculo = microchips.findByAnimal_IdAndDataDesvinculacaoIsNull(animal.getId())
                        .orElseThrow(() -> new ExternalServiceException(
                                "O animal não possui microchip RFID ativo para envio à API governamental."));
                integracao.associarVinculoMicrochip(vinculo);
            }
            var veterinario = vacina.getVeterinario();
            var pessoa = veterinario.getPessoa();
            var solicitacao = new SolicitacaoVacinaAntirrabica(
                    integracao.getChaveIdempotencia(),
                    vinculo.getIdentificador(),
                    animal.getId(),
                    animal.getNome(),
                    vacina.getDataAplicacao(),
                    vacina.getId(),
                    vacina.getNome(),
                    vacina.getFabricante(),
                    vacina.getNumeroLote(),
                    veterinario.getId(),
                    pessoa.getNomeCompleto(),
                    veterinario.getNumeroCrmv(),
                    veterinario.getUfCrmv());
            var resultado = governo.enviarVacinaAntirrabica(solicitacao);
            if (resultado == null || resultado.protocolo() == null || resultado.protocolo().isBlank()) {
                throw new ExternalServiceException("A API governamental simulada não retornou um protocolo.");
            }
            integracao.confirmar(resultado.protocolo(), LocalDateTime.now());
        } catch (ExternalServiceException ex) {
            integracao.falhar(ex.getMessage(), LocalDateTime.now());
            log.warn("Falha na integração governamental {}: {}", integracao.getId(), ex.getMessage());
        }
        return ApiMapper.integracao(integracao);
    }

    @Transactional(readOnly = true)
    public List<IntegracaoResponse> porAnimal(Long animalId) {
        if (!animais.existsById(animalId)) throw new NotFoundException("Animal não encontrado.");
        return integracoes.findAllByAnimal_IdOrderByDataHoraSolicitacaoDesc(animalId).stream()
                .map(ApiMapper::integracao).toList();
    }
    @Transactional(readOnly = true)
    public IntegracaoResponse buscar(Long id) {
        return ApiMapper.integracao(integracoes.findById(id)
                .orElseThrow(() -> new NotFoundException("Operação de integração não encontrada.")));
    }
}
