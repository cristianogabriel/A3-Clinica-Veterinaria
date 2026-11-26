package com.clinica.ibmr.integration;

import com.clinica.ibmr.api.ApiDtos.ViaCepResponse;
import com.clinica.ibmr.api.ApiExceptions.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ViaCepClient {
    private final RestClient client;

    public ViaCepClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.client = RestClient.builder().baseUrl("https://viacep.com.br").requestFactory(requestFactory).build();
    }

    public ViaCepResponse consultar(String cep) {
        try {
            ViaCepResult result = client.get().uri("/ws/{cep}/json/", cep).retrieve().body(ViaCepResult.class);
            if (result == null || Boolean.TRUE.equals(result.erro())) {
                return new ViaCepResponse(false, cep, null, null, null, null, null, null, "CEP não encontrado.");
            }
            return new ViaCepResponse(true, result.cep(), result.logradouro(), result.complemento(), result.bairro(),
                    result.localidade(), result.uf(), result.ibge(), null);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("ViaCEP indisponível. O endereço pode ser informado manualmente.");
        }
    }

    private record ViaCepResult(String cep, String logradouro, String complemento, String bairro,
            @JsonProperty("localidade") String localidade, String uf, String ibge, Boolean erro) {}
}
