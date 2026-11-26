package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.Especie;
import com.clinica.ibmr.domain.Raca;
import com.clinica.ibmr.repository.EspecieRepository;
import com.clinica.ibmr.repository.RacaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogoService {
    private final EspecieRepository especies;
    private final RacaRepository racas;
    public CatalogoService(EspecieRepository especies, RacaRepository racas) {
        this.especies = especies; this.racas = racas;
    }

    @Transactional
    public EspecieResponse cadastrarEspecie(EspecieRequest request) {
        return ApiMapper.especie(especies.save(new Especie(request.nome().trim(), request.descricao())));
    }
    @Transactional(readOnly = true)
    public List<EspecieResponse> listarEspecies() {
        return especies.findAll().stream().map(ApiMapper::especie).toList();
    }
    @Transactional(readOnly = true)
    public EspecieResponse buscarEspecie(Long id) {
        return ApiMapper.especie(especies.findById(id)
                .orElseThrow(() -> new NotFoundException("Espécie não encontrada.")));
    }
    @Transactional
    public RacaResponse cadastrarRaca(RacaRequest request) {
        Especie especie = especies.findById(request.especieId())
                .orElseThrow(() -> new NotFoundException("Espécie não encontrada."));
        if (especie.getSituacao() != com.clinica.ibmr.domain.ClinicaEnums.SituacaoCadastro.ATIVO) {
            throw new InvalidRequestException("Não é possível cadastrar raça para espécie inativa.");
        }
        return ApiMapper.raca(racas.save(new Raca(especie, request.nome().trim(), request.descricao())));
    }
    @Transactional(readOnly = true)
    public List<RacaResponse> listarRacas(Long especieId) {
        List<Raca> result = especieId == null ? racas.findAll() : racas.findAllByEspecie_IdOrderByNomeAsc(especieId);
        return result.stream().map(ApiMapper::raca).toList();
    }
}
