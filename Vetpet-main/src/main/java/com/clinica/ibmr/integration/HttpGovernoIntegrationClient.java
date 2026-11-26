package com.clinica.ibmr.integration;

import com.clinica.ibmr.api.ApiExceptions.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HttpGovernoIntegrationClient implements GovernoIntegrationClient {
    private final RestClient restClient;

    public HttpGovernoIntegrationClient(
            @Value("${integracao.governo.base-url:http://localhost:8081}") String baseUrl) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public ResultadoEnvio enviarVacinaAntirrabica(SolicitacaoVacinaAntirrabica solicitacao) {
        try {
            GovernoVacinacaoRequest request = new GovernoVacinacaoRequest(
                    solicitacao.microchip(), solicitacao.nomeAnimal(), solicitacao.nomeVacina(),
                    solicitacao.fabricante(), solicitacao.lote(), solicitacao.dataAplicacao(),
                    solicitacao.numeroCrmv(), solicitacao.ufCrmv(), solicitacao.nomeVeterinario());
            GovernoVacinacaoResponse response = restClient.post()
                    .uri("/api/governo/vacinacoes-antirrabicas")
                    .header("Idempotency-Key", solicitacao.idempotencyKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GovernoVacinacaoResponse.class);
            if (response == null || response.protocolo() == null || response.protocolo().isBlank()) {
                throw new ExternalServiceException(
                        "A API governamental simulada retornou uma resposta sem protocolo.");
            }
            return new ResultadoEnvio(response.protocolo(), "Vacinação recebida.");
        } catch (RestClientException ex) {
            throw new ExternalServiceException(
                    "Falha ao enviar vacinação à API governamental simulada: " + ex.getMessage());
        }
    }

    private record GovernoVacinacaoRequest(
            @JsonProperty("codigo_microchip") String codigoMicrochip,
            @JsonProperty("nome_animal") String nomeAnimal,
            @JsonProperty("nome_vacina") String nomeVacina,
            String fabricante,
            @JsonProperty("numero_lote") String numeroLote,
            @JsonProperty("data_aplicacao") java.time.LocalDate dataAplicacao,
            String crmv,
            @JsonProperty("uf_crmv") String ufCrmv,
            @JsonProperty("nome_vet_responsavel") String nomeVetResponsavel) {}

    private record GovernoVacinacaoResponse(String protocolo) {}
}
