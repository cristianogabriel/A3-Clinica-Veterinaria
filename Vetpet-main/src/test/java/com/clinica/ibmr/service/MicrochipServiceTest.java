package com.clinica.ibmr.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.repository.VinculoMicrochipRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MicrochipServiceTest {
    @Test
    void substitutionClosesExistingLinkAndPreservesItInTheRepository() {
        AnimalService animals = mock(AnimalService.class);
        VeterinarioService veterinarios = mock(VeterinarioService.class);
        VinculoMicrochipRepository links = mock(VinculoMicrochipRepository.class);
        MicrochipService service = new MicrochipService(animals, veterinarios, links);
        Animal animal = mock(Animal.class);
        Veterinario veterinarian = mock(Veterinario.class);
        VinculoMicrochip previous = mock(VinculoMicrochip.class);
        when(animal.getId()).thenReturn(7L);
        when(veterinarian.getId()).thenReturn(3L);
        when(animals.obterEntidadeAtivo(7L)).thenReturn(animal);
        when(veterinarios.obterAtivo("vet@clinica.test")).thenReturn(veterinarian);
        when(links.existsByIdentificador("985112345678901")).thenReturn(false);
        when(links.findByAnimal_IdAndDataDesvinculacaoIsNull(7L)).thenReturn(Optional.of(previous));
        when(links.save(any(VinculoMicrochip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.vincular(7L, "985112345678901", "vet@clinica.test");

        verify(previous).desvincular(any());
        verify(links).save(any(VinculoMicrochip.class));
        assertTrue(result.ativo());
    }
}
