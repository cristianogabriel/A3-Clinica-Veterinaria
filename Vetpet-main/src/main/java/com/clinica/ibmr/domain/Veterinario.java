package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Veterinarios", uniqueConstraints = @UniqueConstraint(columnNames = {"Numero_CRMV", "UF_CRMV"}))
public class Veterinario {
    @Id
    @Column(name = "fk_Pessoas_ID")
    private Long id;
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "fk_Pessoas_ID")
    private Pessoa pessoa;
    @Column(name = "Numero_CRMV", nullable = false, length = 10)
    private String numeroCrmv;
    @Column(name = "UF_CRMV", nullable = false, length = 2)
    private String ufCrmv;
    @Column(name = "Hash_Senha", nullable = false, length = 255)
    private String hashSenha;
    @Column(name = "Senha_Confirmada", nullable = false)
    private boolean senhaConfirmada;
    @Column(name = "Data_Hora_Confirmacao")
    private LocalDateTime dataHoraConfirmacao;
    @Column(name = "Codigo_Confirmacao_Hash", length = 255)
    private String codigoConfirmacaoHash;
    @Column(name = "Data_Hora_Expiracao_Codigo")
    private LocalDateTime dataHoraExpiracaoCodigo;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao_Veterinario", nullable = false, length = 10)
    private ClinicaEnums.SituacaoCadastro situacao = ClinicaEnums.SituacaoCadastro.ATIVO;
    @Column(name = "Data_Hora_Ultimo_Acesso")
    private LocalDateTime dataHoraUltimoAcesso;

    protected Veterinario() {}
    public Veterinario(Pessoa pessoa, String numeroCrmv, String ufCrmv, String hashSenha) {
        this.pessoa = pessoa; this.numeroCrmv = numeroCrmv; this.ufCrmv = ufCrmv; this.hashSenha = hashSenha;
    }
    public Long getId() { return id; }
    public Pessoa getPessoa() { return pessoa; }
    public String getNumeroCrmv() { return numeroCrmv; }
    public String getUfCrmv() { return ufCrmv; }
    public String getHashSenha() { return hashSenha; }
    public boolean isSenhaConfirmada() { return senhaConfirmada; }
    public LocalDateTime getDataHoraConfirmacao() { return dataHoraConfirmacao; }
    public String getCodigoConfirmacaoHash() { return codigoConfirmacaoHash; }
    public LocalDateTime getDataHoraExpiracaoCodigo() { return dataHoraExpiracaoCodigo; }
    public ClinicaEnums.SituacaoCadastro getSituacao() { return situacao; }
    public LocalDateTime getDataHoraUltimoAcesso() { return dataHoraUltimoAcesso; }
    public void definirConfirmacao(String hash, LocalDateTime expiracao) {
        this.codigoConfirmacaoHash = hash; this.dataHoraExpiracaoCodigo = expiracao;
    }
    public void confirmarSenha(LocalDateTime quando) {
        senhaConfirmada = true; dataHoraConfirmacao = quando;
        codigoConfirmacaoHash = null; dataHoraExpiracaoCodigo = null;
    }
    public void registrarAcesso(LocalDateTime quando) { dataHoraUltimoAcesso = quando; }
    public void alterarSituacao(ClinicaEnums.SituacaoCadastro situacao) { this.situacao = situacao; }
    public void atualizarDadosProfissionais(String numeroCrmv, String ufCrmv) {
        this.numeroCrmv = numeroCrmv; this.ufCrmv = ufCrmv;
    }
}
