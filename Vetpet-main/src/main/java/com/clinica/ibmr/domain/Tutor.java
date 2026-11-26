package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Tutores")
public class Tutor {
    @Id
    @Column(name = "fk_Pessoas_ID")
    private Long id;
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "fk_Pessoas_ID")
    private Pessoa pessoa;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Enderecos_ID", nullable = false)
    private Endereco endereco;
    @Column(name = "Data_Nascimento", nullable = false)
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao_Cadastro", nullable = false, length = 10)
    private ClinicaEnums.SituacaoCadastro situacao = ClinicaEnums.SituacaoCadastro.ATIVO;

    protected Tutor() {}
    public Tutor(Pessoa pessoa, Endereco endereco, LocalDate dataNascimento) {
        this.pessoa = pessoa; this.endereco = endereco; this.dataNascimento = dataNascimento;
    }
    public Long getId() { return id; }
    public Pessoa getPessoa() { return pessoa; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public ClinicaEnums.SituacaoCadastro getSituacao() { return situacao; }
    public void inativar() { this.situacao = ClinicaEnums.SituacaoCadastro.INATIVO; }
}
