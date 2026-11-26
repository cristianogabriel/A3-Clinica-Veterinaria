package com.api.governo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "animal_gov", uniqueConstraints = @UniqueConstraint(name = "uk_animal_microchip", columnNames = "codigo_microchip"))
public class AnimalGov {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_animal")
    private Long id;
    @Column(name = "codigo_microchip", nullable = false, length = 30, unique = true)
    private String codigoMicrochip;
    @Column(length = 100) private String nome;
    @Column(name = "data_nascimento") private LocalDate dataNascimento;
    @Column(length = 20) private String sexo;
    @Column(length = 60) private String cor;
    @Column(length = 60) private String especie;
    @Column(length = 80) private String raca;
    @Column(nullable = false, length = 20) private String situacao = "ATIVO";
    @Column(name = "data_cadastro", nullable = false, updatable = false) private LocalDateTime dataCadastro;
    @Column(name = "data_atualizacao") private LocalDateTime dataAtualizacao;

    @PrePersist void beforeInsert() { dataCadastro = LocalDateTime.now(); if (situacao == null) situacao = "ATIVO"; }
    public Long getId() { return id; }
    public String getCodigoMicrochip() { return codigoMicrochip; }
    public void setCodigoMicrochip(String v) { codigoMicrochip = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { nome = v; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate v) { dataNascimento = v; }
    public String getSexo() { return sexo; }
    public void setSexo(String v) { sexo = v; }
    public String getCor() { return cor; }
    public void setCor(String v) { cor = v; }
    public String getEspecie() { return especie; }
    public void setEspecie(String v) { especie = v; }
    public String getRaca() { return raca; }
    public void setRaca(String v) { raca = v; }
    public String getSituacao() { return situacao; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime v) { dataAtualizacao = v; }
}
