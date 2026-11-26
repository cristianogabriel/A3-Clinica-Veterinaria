package com.clinica.ibmr.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.clinica.ibmr.api.ApiDtos.AnimalRequest;
import com.clinica.ibmr.api.ApiExceptions.InvalidRequestException;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.repository.*;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AnimalServiceTest {
    @Test
    void rejectsBreedThatDoesNotBelongToSelectedSpecies() {
        AnimalRepository animals = mock(AnimalRepository.class);
        TutorRepository tutors = mock(TutorRepository.class);
        EspecieRepository species = mock(EspecieRepository.class);
        RacaRepository breeds = mock(RacaRepository.class);
        AnimalService service = new AnimalService(animals, tutors, species, breeds);

        Tutor tutor = mock(Tutor.class);
        Especie dog = mock(Especie.class);
        Especie cat = mock(Especie.class);
        Raca catBreed = mock(Raca.class);
        when(tutors.findById(1L)).thenReturn(Optional.of(tutor));
        when(tutor.getSituacao()).thenReturn(ClinicaEnums.SituacaoCadastro.ATIVO);
        when(species.findById(2L)).thenReturn(Optional.of(dog));
        when(dog.getId()).thenReturn(2L);
        when(dog.getSituacao()).thenReturn(ClinicaEnums.SituacaoCadastro.ATIVO);
        when(breeds.findById(3L)).thenReturn(Optional.of(catBreed));
        when(catBreed.getEspecie()).thenReturn(cat);
        when(cat.getId()).thenReturn(4L);

        AnimalRequest request = new AnimalRequest(1L, 2L, 3L, "Thor", LocalDate.of(2021, 1, 1),
                null, ClinicaEnums.Sexo.M, "Caramelo", null);

        assertThrows(InvalidRequestException.class, () -> service.cadastrar(request));
        verify(animals, never()).save(any(Animal.class));
    }
}
