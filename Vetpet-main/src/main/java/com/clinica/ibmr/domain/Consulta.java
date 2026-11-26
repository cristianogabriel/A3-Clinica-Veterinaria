package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Consultas")
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Animais_ID_Interno", nullable = false)
    private Animal animal;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Veterinarios_fk_Pessoas_ID", nullable = false)
    private Veterinario veterinario;
    @Column(name = "Data_Hora_Consulta", nullable = false)
    private LocalDateTime dataHora;
    @Column(name = "Motivo_Atendimento", nullable = false, length = 255)
    private String motivo;
    @Column(name = "Anamnese", columnDefinition = "TEXT")
    private String anamnese;
    @Column(name = "Sintomas", columnDefinition = "TEXT")
    private String sintomas;
    @Column(name = "Diagnostico", columnDefinition = "TEXT")
    private String diagnostico;
    @Column(name = "Tratamento_Prescrito", columnDefinition = "TEXT")
    private String tratamentoPrescrito;
    @Column(name = "Prescricao_Medicamentos", columnDefinition = "TEXT")
    private String prescricaoMedicamentos;
    @Column(name = "Peso_Registrado", precision = 6, scale = 2)
    private BigDecimal pesoRegistrado;
    @Column(name = "Temperatura", precision = 4, scale = 1)
    private BigDecimal temperatura;
    @Column(name = "Observacoes", columnDefinition = "TEXT")
    private String observacoes;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao", nullable = false, length = 15)
    private ClinicaEnums.SituacaoConsulta situacao = ClinicaEnums.SituacaoConsulta.AGENDADA;

    protected Consulta() {}
    public Consulta(Animal animal, Veterinario veterinario, LocalDateTime dataHora, String motivo,
                    String anamnese, String sintomas, String diagnostico, String tratamentoPrescrito,
                    String prescricaoMedicamentos, BigDecimal pesoRegistrado, BigDecimal temperatura, String observacoes,
                    ClinicaEnums.SituacaoConsulta situacao) {
        this.animal = animal; this.veterinario = veterinario; this.dataHora = dataHora; this.motivo = motivo;
        this.anamnese = anamnese; this.sintomas = sintomas; this.diagnostico = diagnostico;
        this.tratamentoPrescrito = tratamentoPrescrito; this.prescricaoMedicamentos = prescricaoMedicamentos;
        this.pesoRegistrado = pesoRegistrado; this.temperatura = temperatura; this.observacoes = observacoes;
        this.situacao = situacao;
    }
    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public Veterinario getVeterinario() { return veterinario; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
    public String getAnamnese() { return anamnese; }
    public String getSintomas() { return sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public String getTratamentoPrescrito() { return tratamentoPrescrito; }
    public String getPrescricaoMedicamentos() { return prescricaoMedicamentos; }
    public BigDecimal getPesoRegistrado() { return pesoRegistrado; }
    public BigDecimal getTemperatura() { return temperatura; }
    public String getObservacoes() { return observacoes; }
    public ClinicaEnums.SituacaoConsulta getSituacao() { return situacao; }
    public void alterarSituacao(ClinicaEnums.SituacaoConsulta situacao) { this.situacao = situacao; }
    public void atualizar(LocalDateTime dataHora, String motivo, String anamnese, String sintomas,
                          String diagnostico, String tratamentoPrescrito, String prescricaoMedicamentos,
                          BigDecimal pesoRegistrado, BigDecimal temperatura, String observacoes,
                          ClinicaEnums.SituacaoConsulta situacao) {
        this.dataHora = dataHora; this.motivo = motivo; this.anamnese = anamnese; this.sintomas = sintomas;
        this.diagnostico = diagnostico; this.tratamentoPrescrito = tratamentoPrescrito;
        this.prescricaoMedicamentos = prescricaoMedicamentos; this.pesoRegistrado = pesoRegistrado;
        this.temperatura = temperatura; this.observacoes = observacoes; this.situacao = situacao;
    }
}
