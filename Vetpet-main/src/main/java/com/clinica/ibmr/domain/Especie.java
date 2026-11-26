package com.clinica.ibmr.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Especies")
public class Especie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "Nome", nullable = false, unique = true, length = 60)
    private String nome;
    @Column(name = "Descricao", length = 255)
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao", nullable = false, length = 10)
    private ClinicaEnums.SituacaoCadastro situacao = ClinicaEnums.SituacaoCadastro.ATIVO;

    protected Especie() {}
    public Especie(String nome, String descricao) { this.nome = nome; this.descricao = descricao; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public ClinicaEnums.SituacaoCadastro getSituacao() { return situacao; }
    public void atualizar(String nome, String descricao) { this.nome = nome; this.descricao = descricao; }
}
