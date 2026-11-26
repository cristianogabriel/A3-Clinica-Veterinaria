package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Animais")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Interno")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Especies_ID", nullable = false)
    private Especie especie;
    @ManyToOne
    @JoinColumn(name = "fk_Racas_ID")
    private Raca raca;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Tutores_fk_Pessoas_ID", nullable = false)
    private Tutor tutor;
    @Column(name = "Nome", nullable = false, length = 80)
    private String nome;
    @Column(name = "Data_nascimento")
    private LocalDate dataNascimento;
    @Column(name = "Idade_Estimada")
    private Integer idadeEstimada;
    @Enumerated(EnumType.STRING)
    @Column(name = "Sexo", nullable = false, length = 1)
    private ClinicaEnums.Sexo sexo;
    @Column(name = "Cor", nullable = false, length = 40)
    private String cor;
    @Column(name = "Peso_Atual", precision = 6, scale = 2)
    private BigDecimal pesoAtual;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao_animal", nullable = false, length = 10)
    private ClinicaEnums.SituacaoAnimal situacao = ClinicaEnums.SituacaoAnimal.ATIVO;

    protected Animal() {}
    public Animal(Especie especie, Raca raca, Tutor tutor, String nome, LocalDate dataNascimento,
                  Integer idadeEstimada, ClinicaEnums.Sexo sexo, String cor, BigDecimal pesoAtual) {
        this.especie = especie; this.raca = raca; this.tutor = tutor; this.nome = nome;
        this.dataNascimento = dataNascimento; this.idadeEstimada = idadeEstimada;
        this.sexo = sexo; this.cor = cor; this.pesoAtual = pesoAtual;
    }
    public void atualizar(Especie especie, Raca raca, Tutor tutor, String nome, LocalDate dataNascimento,
                         Integer idadeEstimada, ClinicaEnums.Sexo sexo, String cor, BigDecimal pesoAtual) {
        this.especie = especie; this.raca = raca; this.tutor = tutor; this.nome = nome;
        this.dataNascimento = dataNascimento; this.idadeEstimada = idadeEstimada;
        this.sexo = sexo; this.cor = cor; this.pesoAtual = pesoAtual;
    }
    public Long getId() { return id; }
    public Especie getEspecie() { return especie; }
    public Raca getRaca() { return raca; }
    public Tutor getTutor() { return tutor; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Integer getIdadeEstimada() { return idadeEstimada; }
    public ClinicaEnums.Sexo getSexo() { return sexo; }
    public String getCor() { return cor; }
    public BigDecimal getPesoAtual() { return pesoAtual; }
    public ClinicaEnums.SituacaoAnimal getSituacao() { return situacao; }
    public void alterarSituacao(ClinicaEnums.SituacaoAnimal situacao) { this.situacao = situacao; }
}
