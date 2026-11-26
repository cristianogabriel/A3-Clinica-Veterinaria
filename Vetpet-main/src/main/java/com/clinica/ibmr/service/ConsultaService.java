package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.domain.Consulta;
import com.clinica.ibmr.repository.ConsultaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaService {
    private final AnimalService animais;
    private final VeterinarioService veterinarios;
    private final ConsultaRepository consultas;
    public ConsultaService(AnimalService animais, VeterinarioService veterinarios, ConsultaRepository consultas) {
        this.animais = animais; this.veterinarios = veterinarios; this.consultas = consultas;
    }

    @Transactional
    public ConsultaResponse registrar(ConsultaRequest request, String emailVeterinario) {
        var animal = animais.obterEntidadeAtivo(request.animalId());
        var vet = veterinarios.obterAtivo(emailVeterinario);
        ClinicaEnums.SituacaoConsulta situacao = request.situacao() == null
                ? ClinicaEnums.SituacaoConsulta.AGENDADA : request.situacao();
        validarRealizada(situacao, request.diagnostico(), request.tratamentoPrescrito());
        Consulta consulta = new Consulta(animal, vet, request.dataHora(), request.motivo().trim(),
                request.anamnese(), request.sintomas(), request.diagnostico(), request.tratamentoPrescrito(),
                request.prescricaoMedicamentos(), request.pesoRegistrado(), request.temperatura(),
                request.observacoes(), situacao);
        return ApiMapper.consulta(consultas.save(consulta));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> historico(Long animalId) {
        animais.buscar(animalId);
        return consultas.findAllByAnimal_IdOrderByDataHoraDesc(animalId).stream()
                .map(ApiMapper::consulta).toList();
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaRequest request, String emailVeterinario) {
        Consulta consulta = consultas.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada."));
        if (!consulta.getAnimal().getId().equals(request.animalId())) {
            throw new InvalidRequestException("Não é permitido transferir uma consulta para outro animal.");
        }
        veterinarios.obterAtivo(emailVeterinario);
        ClinicaEnums.SituacaoConsulta situacao = request.situacao() == null
                ? consulta.getSituacao() : request.situacao();
        validarTransicao(consulta.getSituacao(), situacao);
        validarRealizada(situacao, request.diagnostico(), request.tratamentoPrescrito());
        consulta.atualizar(request.dataHora(), request.motivo().trim(), request.anamnese(), request.sintomas(),
                request.diagnostico(), request.tratamentoPrescrito(), request.prescricaoMedicamentos(),
                request.pesoRegistrado(), request.temperatura(), request.observacoes(), situacao);
        return ApiMapper.consulta(consulta);
    }

    @Transactional
    public ConsultaResponse alterarSituacao(Long id, ClinicaEnums.SituacaoConsulta situacao) {
        Consulta consulta = consultas.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada."));
        validarTransicao(consulta.getSituacao(), situacao);
        validarRealizada(situacao, consulta.getDiagnostico(), consulta.getTratamentoPrescrito());
        consulta.alterarSituacao(situacao);
        return ApiMapper.consulta(consulta);
    }

    private void validarRealizada(ClinicaEnums.SituacaoConsulta situacao, String diagnostico, String tratamento) {
        if (situacao == ClinicaEnums.SituacaoConsulta.REALIZADA
                && (diagnostico == null || diagnostico.isBlank() || tratamento == null || tratamento.isBlank())) {
            throw new InvalidRequestException("Consultas realizadas exigem diagnóstico e tratamento prescrito.");
        }
    }

    private void validarTransicao(ClinicaEnums.SituacaoConsulta atual, ClinicaEnums.SituacaoConsulta nova) {
        if (atual != ClinicaEnums.SituacaoConsulta.AGENDADA && atual != nova) {
            throw new InvalidRequestException("Somente consultas agendadas podem mudar para outra situação.");
        }
    }
}
