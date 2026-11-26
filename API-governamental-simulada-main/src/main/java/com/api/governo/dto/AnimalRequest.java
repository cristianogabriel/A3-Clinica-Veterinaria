package com.api.governo.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record AnimalRequest(@NotBlank @Size(max=30) String codigoMicrochip, @Size(max=100) String nome,
 @PastOrPresent LocalDate dataNascimento, @Size(max=20) String sexo, @Size(max=60) String cor,
 @Size(max=60) String especie, @Size(max=80) String raca) { }
