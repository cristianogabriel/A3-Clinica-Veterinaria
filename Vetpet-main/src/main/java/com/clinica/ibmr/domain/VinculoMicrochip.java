package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Vinculo_microchip")
public class VinculoMicrochip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Vinculo")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Veterinarios_fk_Pessoas_ID", nullable = false)
    private Veterinario veterinario;
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_Animais_ID_Interno", nullable = false)
    private Animal animal;
    @CreationTimestamp
    @Column(name = "Data_Vinculacao_Microchip", nullable = false, updatable = false)
    private LocalDateTime dataVinculacao;
    @Column(name = "ID_Microchip_RFID", nullable = false, unique = true, length = 20)
    private String identificador;
    @Column(name = "Data_Desvinculacao")
    private LocalDateTime dataDesvinculacao;

    protected VinculoMicrochip() {}
    public VinculoMicrochip(Veterinario veterinario, Animal animal, String identificador) {
        this.veterinario = veterinario; this.animal = animal; this.identificador = identificador;
    }
    public Long getId() { return id; }
    public Veterinario getVeterinario() { return veterinario; }
    public Animal getAnimal() { return animal; }
    public LocalDateTime getDataVinculacao() { return dataVinculacao; }
    public String getIdentificador() { return identificador; }
    public LocalDateTime getDataDesvinculacao() { return dataDesvinculacao; }
    public boolean isAtivo() { return dataDesvinculacao == null; }
    public void desvincular(LocalDateTime quando) { dataDesvinculacao = quando; }
}
