package com.clinica.ibmr.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Enderecos")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CEP", nullable = false, length = 8)
    private String cep;
    @Column(name = "Logradouro", nullable = false, length = 150)
    private String logradouro;
    @Column(name = "Numero", nullable = false, length = 10)
    private String numero;
    @Column(name = "Complemento", length = 80)
    private String complemento;
    @Column(name = "Bairro", nullable = false, length = 80)
    private String bairro;
    @Column(name = "Cidade", nullable = false, length = 80)
    private String cidade;
    @Column(name = "UF", nullable = false, length = 2)
    private String uf;
    @Column(name = "Codigo_IBGE_Municipio", length = 7)
    private String codigoIbgeMunicipio;

    protected Endereco() {}
    public Endereco(String cep, String logradouro, String numero, String complemento, String bairro,
                    String cidade, String uf, String codigoIbgeMunicipio) {
        atualizar(cep, logradouro, numero, complemento, bairro, cidade, uf, codigoIbgeMunicipio);
    }
    public void atualizar(String cep, String logradouro, String numero, String complemento, String bairro,
                          String cidade, String uf, String codigoIbgeMunicipio) {
        this.cep = cep; this.logradouro = logradouro; this.numero = numero; this.complemento = complemento;
        this.bairro = bairro; this.cidade = cidade; this.uf = uf; this.codigoIbgeMunicipio = codigoIbgeMunicipio;
    }
    public Long getId() { return id; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public String getCodigoIbgeMunicipio() { return codigoIbgeMunicipio; }
}
