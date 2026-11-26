package com.clinica.ibmr.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pessoas")
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "Nome_Completo", nullable = false, length = 150)
    private String nomeCompleto;
    @Column(name = "CPF", nullable = false, unique = true, length = 11)
    private String cpf;
    @Column(name = "Email", nullable = false, unique = true, length = 150)
    private String email;
    @Column(name = "Telefone", nullable = false, length = 20)
    private String telefone;
    @CreationTimestamp
    @Column(name = "Data_Cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    protected Pessoa() {}
    public Pessoa(String nomeCompleto, String cpf, String email, String telefone) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public Long getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
}
