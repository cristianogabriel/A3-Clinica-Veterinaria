package com.api.governo.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record VacinacaoRequest(@NotBlank @Size(max=30) String codigoMicrochip, @NotBlank @Size(max=100) String nomeAnimal,
 @Size(max=60) String especie, @Size(max=60) String cor, @Size(max=20) String sexo, @Size(max=80) String raca,
 @PastOrPresent LocalDate dataNascimento, @NotBlank @Size(max=100) String nomeVacina, @Size(max=100) String fabricante,
 @NotBlank @Size(max=60) String numeroLote, @NotNull LocalDate dataAplicacao, @Size(max=50) String dose,
 @Size(max=50) String viaAplicacao, LocalDate validadeLote, String observacoes,
 @NotBlank @Size(max=10) String crmv, @NotBlank @Size(min=2,max=2) String ufCrmv,
 @NotBlank @Size(max=150) String nomeVetResponsavel) { }
