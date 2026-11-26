package com.clinica.ibmr.config;

import com.clinica.ibmr.domain.Especie;
import com.clinica.ibmr.domain.Raca;
import com.clinica.ibmr.repository.EspecieRepository;
import com.clinica.ibmr.repository.RacaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Cria espécies e raças iniciais quando o catálogo está vazio (ativado por app.seed.catalogo=true). */
@Component
public class CatalogoSeed implements ApplicationRunner {
    private final EspecieRepository especies;
    private final RacaRepository racas;
    @Value("${app.seed.catalogo:false}")
    private boolean ativo;

    public CatalogoSeed(EspecieRepository especies, RacaRepository racas) {
        this.especies = especies;
        this.racas = racas;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!ativo || especies.count() > 0) return;
        cadastrar("Cão", "Labrador", "Golden Retriever", "Poodle", "Shih-tzu");
        cadastrar("Gato", "Siamês", "Persa", "Maine Coon");
        cadastrar("Ave", "Calopsita", "Periquito", "Canário");
        cadastrar("Outro");
    }

    private void cadastrar(String especie, String... nomesRacas) {
        Especie salva = especies.save(new Especie(especie, null));
        for (String nome : nomesRacas) racas.save(new Raca(salva, nome, null));
    }
}
