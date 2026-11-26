package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Vacinas")
public class Vacina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Aplicacao")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Animais_ID_Interno", nullable = false)
    private Animal animal;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Veterinarios_fk_Pessoas_ID", nullable = false)
    private Veterinario veterinario;
    @Column(name = "Nome_Vacina", nullable = false, length = 100)
    private String nome;
    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo_Vacina", nullable = false, length = 20)
    private ClinicaEnums.TipoVacina tipo;
    @Column(name = "Eh_Antirrabica", nullable = false)
    private boolean antirrabica;
    @Column(name = "Fabricante", nullable = false, length = 100)
    private String fabricante;
    @Column(name = "Numero_Lote", nullable = false, length = 40)
    private String numeroLote;
    @Column(name = "Data_aplicacao")
    private LocalDate dataAplicacao;
    @Column(name = "Dose_Etapa_Esquema", nullable = false, length = 30)
    private String doseEtapa;
    @Column(name = "Via_Aplicacao", nullable = false, length = 30)
    private String viaAplicacao;
    @Column(name = "Data_Proxima_Dose")
    private LocalDate dataProximaDose;
    @Column(name = "Data_Validade_Lote", nullable = false)
    private LocalDate dataValidadeLote;
    @Column(name = "Observacao", columnDefinition = "TEXT")
    private String observacao;
    @Enumerated(EnumType.STRING)
    @Column(name = "Situacao_Aplicacao", nullable = false, length = 10)
    private ClinicaEnums.SituacaoVacina situacao = ClinicaEnums.SituacaoVacina.AGENDADA;

    protected Vacina() {}
    public Vacina(Animal animal, Veterinario veterinario, String nome, ClinicaEnums.TipoVacina tipo,
                  String fabricante, String numeroLote, LocalDate dataAplicacao, String doseEtapa, String viaAplicacao,
                  LocalDate dataProximaDose, LocalDate dataValidadeLote, String observacao,
                  ClinicaEnums.SituacaoVacina situacao) {
        this.animal = animal; this.veterinario = veterinario; this.nome = nome; this.tipo = tipo;
        this.antirrabica = tipo == ClinicaEnums.TipoVacina.ANTIRRABICA; this.fabricante = fabricante;
        this.numeroLote = numeroLote; this.dataAplicacao = dataAplicacao; this.doseEtapa = doseEtapa;
        this.viaAplicacao = viaAplicacao; this.dataProximaDose = dataProximaDose;
        this.dataValidadeLote = dataValidadeLote; this.observacao = observacao; this.situacao = situacao;
    }
    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public Veterinario getVeterinario() { return veterinario; }
    public String getNome() { return nome; }
    public ClinicaEnums.TipoVacina getTipo() { return tipo; }
    public boolean isAntirrabica() { return antirrabica; }
    public String getFabricante() { return fabricante; }
    public String getNumeroLote() { return numeroLote; }
    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public String getDoseEtapa() { return doseEtapa; }
    public String getViaAplicacao() { return viaAplicacao; }
    public LocalDate getDataProximaDose() { return dataProximaDose; }
    public LocalDate getDataValidadeLote() { return dataValidadeLote; }
    public String getObservacao() { return observacao; }
    public ClinicaEnums.SituacaoVacina getSituacao() { return situacao; }
    public void aplicar(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
        this.situacao = ClinicaEnums.SituacaoVacina.APLICADA;
    }
    public void cancelar() { this.situacao = ClinicaEnums.SituacaoVacina.CANCELADA; }
}
