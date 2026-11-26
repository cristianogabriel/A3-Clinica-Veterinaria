package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.integration.VacinaAntirrabicaAplicadaEvent;
import com.clinica.ibmr.repository.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VacinaService {
    private final AnimalService animais;
    private final VeterinarioService veterinarios;
    private final VacinaRepository vacinas;
    private final IntegracaoGovernoRepository integracoes;
    private final VinculoMicrochipRepository microchips;
    private final ApplicationEventPublisher eventos;
    public VacinaService(AnimalService animais, VeterinarioService veterinarios, VacinaRepository vacinas,
                        IntegracaoGovernoRepository integracoes, VinculoMicrochipRepository microchips,
                        ApplicationEventPublisher eventos) {
        this.animais = animais; this.veterinarios = veterinarios; this.vacinas = vacinas;
        this.integracoes = integracoes; this.microchips = microchips; this.eventos = eventos;
    }

    @Transactional
    public VacinaResponse registrar(VacinaRequest request, String emailVeterinario) {
        var animal = animais.obterEntidadeAtivo(request.animalId());
        var vet = veterinarios.obterAtivo(emailVeterinario);
        ClinicaEnums.SituacaoVacina situacao = request.situacao() == null
                ? ClinicaEnums.SituacaoVacina.AGENDADA : request.situacao();
        validarDados(request.dataAplicacao(), request.dataProximaDose(), request.dataValidadeLote(), situacao);
        Vacina vacina = new Vacina(animal, vet, request.nome().trim(), request.tipo(), request.fabricante().trim(),
                request.numeroLote().trim(), request.dataAplicacao(), request.doseEtapa().trim(),
                request.viaAplicacao().trim(), request.dataProximaDose(), request.dataValidadeLote(),
                request.observacao(), situacao);
        vacina = vacinas.save(vacina);
        if (situacao == ClinicaEnums.SituacaoVacina.APLICADA && vacina.isAntirrabica()) {
            registrarPendente(vacina);
        }
        return ApiMapper.vacina(vacina);
    }

    @Transactional(readOnly = true)
    public List<VacinaResponse> historico(Long animalId) {
        animais.buscar(animalId);
        return vacinas.findAllByAnimal_IdOrderByDataAplicacaoDesc(animalId).stream()
                .map(ApiMapper::vacina).toList();
    }

    @Transactional(readOnly = true)
    public List<ProximaDoseResponse> proximas(Long animalId, int dias) {
        if (dias < 1 || dias > 365) throw new InvalidRequestException("O período deve estar entre 1 e 365 dias.");
        if (animalId != null) animais.buscar(animalId);
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return vacinas.findAllBySituacaoInAndDataProximaDoseGreaterThanEqualOrderByDataProximaDoseAsc(
                        List.of(ClinicaEnums.SituacaoVacina.APLICADA, ClinicaEnums.SituacaoVacina.AGENDADA), hoje)
                .stream()
                .filter(v -> !v.getDataProximaDose().isAfter(limite))
                .filter(v -> animalId == null || v.getAnimal().getId().equals(animalId))
                .map(v -> new ProximaDoseResponse(v.getId(), v.getAnimal().getId(), v.getAnimal().getNome(),
                        v.getNome(), v.getDataProximaDose(), v.getDoseEtapa()))
                .toList();
    }

    @Transactional
    public VacinaResponse alterarSituacao(Long vacinaId, SituacaoVacinaRequest request) {
        Vacina vacina = vacinas.findById(vacinaId)
                .orElseThrow(() -> new NotFoundException("Aplicação de vacina não encontrada."));
        if (vacina.getSituacao() != ClinicaEnums.SituacaoVacina.AGENDADA) {
            throw new InvalidRequestException("Somente vacinas agendadas podem mudar de situação.");
        }
        if (request.situacao() == ClinicaEnums.SituacaoVacina.APLICADA) {
            if (request.dataAplicacao() == null) {
                throw new InvalidRequestException("Informe a data de aplicação para concluir a vacinação.");
            }
            validarDados(request.dataAplicacao(), vacina.getDataProximaDose(), vacina.getDataValidadeLote(),
                    ClinicaEnums.SituacaoVacina.APLICADA);
            vacina.aplicar(request.dataAplicacao());
            if (vacina.isAntirrabica()) registrarPendente(vacina);
        } else if (request.situacao() == ClinicaEnums.SituacaoVacina.CANCELADA) {
            vacina.cancelar();
        } else {
            throw new InvalidRequestException("A vacinação já está agendada.");
        }
        return ApiMapper.vacina(vacina);
    }

    private void registrarPendente(Vacina vacina) {
        if (integracoes.existsByVacina_Id(vacina.getId())) return;
        IntegracaoGoverno integracao = new IntegracaoGoverno(vacina.getAnimal(), vacina,
                "vacina-antirrabica-" + vacina.getId());
        microchips.findByAnimal_IdAndDataDesvinculacaoIsNull(vacina.getAnimal().getId())
                .ifPresent(integracao::associarVinculoMicrochip);
        integracoes.save(integracao);
        eventos.publishEvent(new VacinaAntirrabicaAplicadaEvent(vacina.getId()));
    }

    private void validarDados(LocalDate dataAplicacao, LocalDate proximaDose, LocalDate validade,
                              ClinicaEnums.SituacaoVacina situacao) {
        if (situacao == ClinicaEnums.SituacaoVacina.APLICADA && dataAplicacao == null) {
            throw new InvalidRequestException("Vacina aplicada exige data de aplicação.");
        }
        if (dataAplicacao != null && dataAplicacao.isAfter(LocalDate.now())) {
            throw new InvalidRequestException("A data de aplicação não pode estar no futuro.");
        }
        if (dataAplicacao != null && validade.isBefore(dataAplicacao)) {
            throw new InvalidRequestException("A validade do lote deve ser posterior à data de aplicação.");
        }
        if (dataAplicacao != null && proximaDose != null && !proximaDose.isAfter(dataAplicacao)) {
            throw new InvalidRequestException("A próxima dose deve ser posterior à aplicação atual.");
        }
    }
}
