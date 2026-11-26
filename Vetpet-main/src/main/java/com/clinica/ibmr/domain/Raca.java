package com.clinica.ibmr.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Racas", uniqueConstraints = @UniqueConstraint(columnNames = {"fk_Especies_ID", "Nome"}))
public class Raca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Especies_ID", nullable = false)
    private Especie especie;
    @Column(name = "Nome", nullable = false, length = 80)
    private String nome;
    @Column(name = "Descricao", length = 255)
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao", nullable = false, length = 10)
    private ClinicaEnums.SituacaoCadastro situacao = ClinicaEnums.SituacaoCadastro.ATIVO;

    protected Raca() {}
    public Raca(Especie especie, String nome, String descricao) {
        this.especie = especie; this.nome = nome; this.descricao = descricao;
    }
    public Long getId() { return id; }
    public Especie getEspecie() { return especie; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public ClinicaEnums.SituacaoCadastro getSituacao() { return situacao; }
}
