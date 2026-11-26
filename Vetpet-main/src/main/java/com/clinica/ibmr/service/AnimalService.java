package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.AnimalRequest;
import com.clinica.ibmr.api.ApiDtos.AnimalResponse;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnimalService {
    private final AnimalRepository animais;
    private final TutorRepository tutores;
    private final EspecieRepository especies;
    private final RacaRepository racas;
    public AnimalService(AnimalRepository animais, TutorRepository tutores,
                         EspecieRepository especies, RacaRepository racas) {
        this.animais = animais; this.tutores = tutores; this.especies = especies; this.racas = racas;
    }

    @Transactional
    public AnimalResponse cadastrar(AnimalRequest request) {
        validarDatas(request);
        return ApiMapper.animal(animais.save(construir(request)));
    }
    @Transactional(readOnly = true)
    public List<AnimalResponse> listar(Long tutorId) {
        List<Animal> result = tutorId == null ? animais.findAll() : animais.findAllByTutor_IdOrderByNomeAsc(tutorId);
        return result.stream().map(ApiMapper::animal).toList();
    }
    @Transactional(readOnly = true)
    public AnimalResponse buscar(Long id) { return ApiMapper.animal(obter(id)); }
    @Transactional
    public AnimalResponse atualizar(Long id, AnimalRequest request) {
        validarDatas(request);
        Animal animal = obter(id);
        Animal dados = construir(request);
        animal.atualizar(dados.getEspecie(), dados.getRaca(), dados.getTutor(), dados.getNome(),
                dados.getDataNascimento(), dados.getIdadeEstimada(), dados.getSexo(), dados.getCor(), dados.getPesoAtual());
        return ApiMapper.animal(animal);
    }
    @Transactional
    public AnimalResponse alterarSituacao(Long id, ClinicaEnums.SituacaoAnimal situacao) {
        Animal animal = obter(id);
        animal.alterarSituacao(situacao);
        return ApiMapper.animal(animal);
    }
    @Transactional(readOnly = true)
    public Animal obterEntidadeAtivo(Long id) {
        Animal animal = obter(id);
        if (animal.getSituacao() != ClinicaEnums.SituacaoAnimal.ATIVO) {
            throw new InvalidRequestException("O animal não está ativo.");
        }
        return animal;
    }
    private Animal construir(AnimalRequest request) {
        Tutor tutor = tutores.findById(request.tutorId())
                .orElseThrow(() -> new NotFoundException("Tutor não encontrado."));
        if (tutor.getSituacao() != ClinicaEnums.SituacaoCadastro.ATIVO) {
            throw new InvalidRequestException("Não é possível vincular animal a tutor inativo.");
        }
        Especie especie = especies.findById(request.especieId())
                .orElseThrow(() -> new NotFoundException("Espécie não encontrada."));
        if (especie.getSituacao() != ClinicaEnums.SituacaoCadastro.ATIVO) {
            throw new InvalidRequestException("Espécie inativa.");
        }
        Raca raca = request.racaId() == null ? null : racas.findById(request.racaId())
                .orElseThrow(() -> new NotFoundException("Raça não encontrada."));
        if (raca != null && !raca.getEspecie().getId().equals(especie.getId())) {
            throw new InvalidRequestException("A raça selecionada não pertence à espécie informada.");
        }
        if (raca != null && raca.getSituacao() != ClinicaEnums.SituacaoCadastro.ATIVO) {
            throw new InvalidRequestException("Raça inativa.");
        }
        return new Animal(especie, raca, tutor, request.nome().trim(), request.dataNascimento(),
                request.idadeEstimada(), request.sexo(), request.cor().trim(), request.pesoAtual());
    }
    private void validarDatas(AnimalRequest request) {
        if (request.dataNascimento() == null && request.idadeEstimada() == null) {
            throw new InvalidRequestException("Informe a data de nascimento ou a idade estimada.");
        }
        if (request.dataNascimento() != null && request.dataNascimento().isAfter(java.time.LocalDate.now())) {
            throw new InvalidRequestException("A data de nascimento não pode estar no futuro.");
        }
    }
    private Animal obter(Long id) {
        return animais.findById(id).orElseThrow(() -> new NotFoundException("Animal não encontrado."));
    }
}
