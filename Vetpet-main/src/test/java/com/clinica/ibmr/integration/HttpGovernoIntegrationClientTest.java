package com.clinica.ibmr.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.clinica.ibmr.integration.GovernoIntegrationClient.SolicitacaoVacinaAntirrabica;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class HttpGovernoIntegrationClientTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void postsGovernmentPayloadWithIdempotencyHeaderAndReturnsProtocol() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> idempotencyKey = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/api/governo/vacinacoes-antirrabicas", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes()));
            idempotencyKey.set(exchange.getRequestHeaders().getFirst("Idempotency-Key"));
            byte[] response = """
                    {"codigoMicrochip":"985112345678901","idVacinacao":31,"protocolo":"PROTOCOLO-31"}
                    """.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            var client = new HttpGovernoIntegrationClient("http://localhost:" + server.getAddress().getPort());
            var resultado = client.enviarVacinaAntirrabica(new SolicitacaoVacinaAntirrabica(
                    "vacina-antirrabica-31", "985112345678901", 9L, "Rex",
                    LocalDate.of(2026, 1, 15), 31L, "Antirrábica", "Fabricante",
                    "LT-31", 4L, "Veterinária", "90001", "RJ"));

            var payload = objectMapper.readTree(requestBody.get());
            assertEquals("vacina-antirrabica-31", idempotencyKey.get());
            assertEquals("985112345678901", payload.get("codigo_microchip").asText());
            assertEquals("Rex", payload.get("nome_animal").asText());
            assertEquals("Antirrábica", payload.get("nome_vacina").asText());
            assertEquals("2026-01-15", payload.get("data_aplicacao").asText());
            assertEquals("90001", payload.get("crmv").asText());
            assertEquals("RJ", payload.get("uf_crmv").asText());
            assertEquals("Veterinária", payload.get("nome_vet_responsavel").asText());
            assertEquals("PROTOCOLO-31", resultado.protocolo());
        } finally {
            server.stop(0);
        }
    }
}
