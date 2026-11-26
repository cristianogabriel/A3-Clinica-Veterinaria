package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.MicrochipResponse;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.VinculoMicrochip;
import com.clinica.ibmr.repository.VinculoMicrochipRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MicrochipService {
    private final AnimalService animais;
    private final VeterinarioService veterinarios;
    private final VinculoMicrochipRepository vinculos;
    public MicrochipService(AnimalService animais, VeterinarioService veterinarios,
                            VinculoMicrochipRepository vinculos) {
        this.animais = animais; this.veterinarios = veterinarios; this.vinculos = vinculos;
    }

    @Transactional
    public MicrochipResponse vincular(Long animalId, String identificador, String emailVeterinario) {
        var animal = animais.obterEntidadeAtivo(animalId);
        var veterinario = veterinarios.obterAtivo(emailVeterinario);
        if (vinculos.existsByIdentificador(identificador)) {
            throw new ConflictException("Identificador de microchip já utilizado.");
        }
        vinculos.findByAnimal_IdAndDataDesvinculacaoIsNull(animalId)
                .ifPresent(anterior -> anterior.desvincular(LocalDateTime.now()));
        return ApiMapper.microchip(vinculos.save(new VinculoMicrochip(veterinario, animal, identificador)));
    }

    @Transactional(readOnly = true)
    public List<MicrochipResponse> historico(Long animalId) {
        animais.buscar(animalId);
        return vinculos.findAllByAnimal_IdOrderByDataVinculacaoDesc(animalId).stream()
                .map(ApiMapper::microchip).toList();
    }
}
