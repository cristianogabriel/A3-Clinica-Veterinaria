package com.api.governo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operacao_gov", uniqueConstraints = {
    @UniqueConstraint(name = "uk_operacao_idempotencia", columnNames = "chave_idempotencia"),
    @UniqueConstraint(name = "uk_operacao_protocolo", columnNames = "protocolo")
})
public class OperacaoGov {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_operacao") private Long id;
    @Column(name = "chave_idempotencia", nullable = false, length = 100, unique = true) private String chaveIdempotencia;
    @Column(name = "tipo_operacao", nullable = false, length = 30) private String tipoOperacao;
    @Column(nullable = false, length = 50, unique = true) private String protocolo;
    @Column(nullable = false, length = 20) private String situacao;
    @Column(name = "data_recebimento", nullable = false) private LocalDateTime dataRecebimento;
    @Column(name = "data_processamento") private LocalDateTime dataProcessamento;
    @Column(columnDefinition = "json") private String resposta;
    @Column(name = "requisicao", nullable = false, columnDefinition = "text") private String requisicao;
    @PrePersist void beforeInsert() { if (dataRecebimento == null) dataRecebimento = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getChaveIdempotencia() { return chaveIdempotencia; }
    public void setChaveIdempotencia(String v) { chaveIdempotencia = v; }
    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String v) { tipoOperacao = v; }
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String v) { protocolo = v; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String v) { situacao = v; }
    public LocalDateTime getDataRecebimento() { return dataRecebimento; }
    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(LocalDateTime v) { dataProcessamento = v; }
    public String getResposta() { return resposta; }
    public void setResposta(String v) { resposta = v; }
    public String getRequisicao() { return requisicao; }
    public void setRequisicao(String v) { requisicao = v; }
}
