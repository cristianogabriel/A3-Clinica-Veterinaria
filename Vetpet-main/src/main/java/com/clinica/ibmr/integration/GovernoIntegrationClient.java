package com.clinica.ibmr.integration;

import java.time.LocalDate;

public interface GovernoIntegrationClient {
    ResultadoEnvio enviarVacinaAntirrabica(SolicitacaoVacinaAntirrabica solicitacao);

    record SolicitacaoVacinaAntirrabica(String idempotencyKey, String microchip, Long animalId,
            String nomeAnimal, LocalDate dataAplicacao, Long vacinaId, String nomeVacina,
            String fabricante, String lote, Long veterinarioId, String nomeVeterinario,
            String numeroCrmv, String ufCrmv) {}
    record ResultadoEnvio(String protocolo, String mensagem) {}
}
