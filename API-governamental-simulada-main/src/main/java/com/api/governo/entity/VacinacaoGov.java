package com.api.governo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vacinacao_gov", uniqueConstraints = @UniqueConstraint(name = "uk_vacinacao_operacao", columnNames = "id_operacao"))
public class VacinacaoGov {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_vacinacao") private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "id_animal", nullable = false) private AnimalGov animal;
    @OneToOne(optional = false) @JoinColumn(name = "id_operacao", nullable = false, unique = true) private OperacaoGov operacao;
    @Column(name = "nome_vacina", nullable = false, length = 100) private String nomeVacina;
    @Column(length = 100) private String fabricante;
    @Column(name = "numero_lote", nullable = false, length = 60) private String numeroLote;
    @Column(name = "data_aplicacao", nullable = false) private LocalDate dataAplicacao;
    @Column(length = 50) private String dose;
    @Column(name = "via_aplicacao", length = 50) private String viaAplicacao;
    @Column(name = "validade_lote") private LocalDate validadeLote;
    @Column(columnDefinition = "text") private String observacoes;
    @Column(name = "CRMV", nullable = false, length = 10) private String crmv;
    @Column(name = "UF_CRMV", nullable = false, length = 2) private String ufCrmv;
    @Column(name = "nome_vet_responsavel", nullable = false, length = 150) private String nomeVetResponsavel;
    public Long getId() { return id; }
    public void setAnimal(AnimalGov v) { animal = v; }
    public void setOperacao(OperacaoGov v) { operacao = v; }
    public void setNomeVacina(String v) { nomeVacina = v; }
    public void setFabricante(String v) { fabricante = v; }
    public void setNumeroLote(String v) { numeroLote = v; }
    public void setDataAplicacao(LocalDate v) { dataAplicacao = v; }
    public void setDose(String v) { dose = v; }
    public void setViaAplicacao(String v) { viaAplicacao = v; }
    public void setValidadeLote(LocalDate v) { validadeLote = v; }
    public void setObservacoes(String v) { observacoes = v; }
    public void setCrmv(String v) { crmv = v; }
    public void setUfCrmv(String v) { ufCrmv = v; }
    public void setNomeVetResponsavel(String v) { nomeVetResponsavel = v; }
}
