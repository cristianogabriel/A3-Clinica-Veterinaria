package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "IntegracaoGoverno")
public class IntegracaoGoverno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Integracao")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Animais_ID_Interno", nullable = false)
    private Animal animal;
    @OneToOne
    @JoinColumn(name = "fk_Vacinas_ID_Aplicacao", unique = true)
    private Vacina vacina;
    @ManyToOne
    @JoinColumn(name = "fk_Vinculo_microchip_ID_Vinculo")
    private VinculoMicrochip vinculoMicrochip;
    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo_Operacao", nullable = false, length = 25)
    private ClinicaEnums.TipoOperacaoIntegracao tipoOperacao;
    @Column(name = "Protocolo_Externo", unique = true, length = 40)
    private String protocoloExterno;
    @Column(name = "Chave_Idempotencia", nullable = false, unique = true, length = 100)
    private String chaveIdempotencia;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao_Integracao", nullable = false, length = 12)
    private ClinicaEnums.SituacaoIntegracao situacao = ClinicaEnums.SituacaoIntegracao.PENDENTE;
    @Column(name = "Quantidade_Tentativas", nullable = false)
    private int quantidadeTentativas;
    @CreationTimestamp
    @Column(name = "Data_Hora_Solicitacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraSolicitacao;
    @Column(name = "Data_Hora_Resposta")
    private LocalDateTime dataHoraResposta;
    @Column(name = "Mensagem_Erro", columnDefinition = "TEXT")
    private String mensagemErro;

    protected IntegracaoGoverno() {}
    public IntegracaoGoverno(Animal animal, Vacina vacina, String chaveIdempotencia) {
        this.animal = animal; this.vacina = vacina; this.chaveIdempotencia = chaveIdempotencia;
        this.tipoOperacao = ClinicaEnums.TipoOperacaoIntegracao.VACINA_ANTIRRABICA;
    }
    public void associarVinculoMicrochip(VinculoMicrochip vinculoMicrochip) {
        this.vinculoMicrochip = vinculoMicrochip;
    }
    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public Vacina getVacina() { return vacina; }
    public VinculoMicrochip getVinculoMicrochip() { return vinculoMicrochip; }
    public ClinicaEnums.TipoOperacaoIntegracao getTipoOperacao() { return tipoOperacao; }
    public String getProtocoloExterno() { return protocoloExterno; }
    public String getChaveIdempotencia() { return chaveIdempotencia; }
    public ClinicaEnums.SituacaoIntegracao getSituacao() { return situacao; }
    public int getQuantidadeTentativas() { return quantidadeTentativas; }
    public LocalDateTime getDataHoraSolicitacao() { return dataHoraSolicitacao; }
    public LocalDateTime getDataHoraResposta() { return dataHoraResposta; }
    public String getMensagemErro() { return mensagemErro; }

    public void iniciarTentativa() {
        quantidadeTentativas++;
        situacao = ClinicaEnums.SituacaoIntegracao.ENVIADA;
        mensagemErro = null;
        dataHoraResposta = null;
    }
    public void confirmar(String protocolo, LocalDateTime quando) {
        protocoloExterno = protocolo;
        situacao = ClinicaEnums.SituacaoIntegracao.CONFIRMADA;
        dataHoraResposta = quando;
        mensagemErro = null;
    }
    public void falhar(String mensagem, LocalDateTime quando) {
        situacao = ClinicaEnums.SituacaoIntegracao.FALHA;
        dataHoraResposta = quando;
        mensagemErro = mensagem;
    }
}
