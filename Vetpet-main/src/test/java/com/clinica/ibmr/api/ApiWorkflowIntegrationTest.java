package com.clinica.ibmr.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.clinica.ibmr.integration.GovernoIntegrationClient;
import com.clinica.ibmr.integration.GovernoIntegrationClient.ResultadoEnvio;
import com.clinica.ibmr.api.ApiExceptions.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ApiWorkflowIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private GovernoIntegrationClient governo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void bootstrapLoginTutorAnimalMicrochipAndRabiesVaccineFlow() throws Exception {
        when(governo.enviarVacinaAntirrabica(any()))
                .thenThrow(new ExternalServiceException("Serviço indisponível."))
                .thenReturn(new ResultadoEnvio("PROTOCOLO-TESTE-1", "Recebida"));
        MvcResult bootstrap = mockMvc.perform(post("/api/auth/bootstrap")
                        .header("X-Bootstrap-Secret", "test-bootstrap-secret")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nomeCompleto":"Veterinaria Teste",
                                  "cpf":"11144477735",
                                  "email":"vet.teste@clinica.exemplo",
                                  "telefone":"21990000001",
                                  "numeroCrmv":"90001",
                                  "ufCrmv":"RJ",
                                  "senha":"senha-de-teste-segura"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String confirmationCode = json(bootstrap).get("codigoConfirmacao").asText();

        mockMvc.perform(post("/api/auth/confirmacao")
                        .contentType("application/json")
                        .content("""
                                {"email":"vet.teste@clinica.exemplo","codigo":"%s"}
                                """.formatted(confirmationCode)))
                .andExpect(status().isNoContent());

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"vet.teste@clinica.exemplo","senha":"senha-de-teste-segura"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        MvcResult species = mockMvc.perform(post("/api/especies")
                        .session(session)
                        .contentType("application/json")
                        .content("""
                                {"nome":"Cao fluxo","descricao":"Teste de API"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long speciesId = json(species).get("id").asLong();

        MvcResult tutor = mockMvc.perform(post("/api/tutores")
                        .session(session)
                        .contentType("application/json")
                        .content("""
                                {
                                  "nomeCompleto":"Tutor de Teste",
                                  "cpf":"52998224725",
                                  "email":"tutor.teste@clinica.exemplo",
                                  "telefone":"21990000002",
                                  "dataNascimento":"1990-04-12",
                                  "endereco":{
                                    "cep":"20040020",
                                    "logradouro":"Rua Teste",
                                    "numero":"10",
                                    "bairro":"Centro",
                                    "cidade":"Rio de Janeiro",
                                    "uf":"RJ",
                                    "codigoIbgeMunicipio":"3304557"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long tutorId = json(tutor).get("id").asLong();

        MvcResult animal = mockMvc.perform(post("/api/animais")
                        .session(session)
                        .contentType("application/json")
                        .content("""
                                {
                                  "tutorId":%d,
                                  "especieId":%d,
                                  "nome":"Rex API",
                                  "dataNascimento":"2022-01-01",
                                  "sexo":"M",
                                  "cor":"Preto",
                                  "pesoAtual":12.5
                                }
                                """.formatted(tutorId, speciesId)))
                .andExpect(status().isCreated())
                .andReturn();
        long animalId = json(animal).get("id").asLong();

        mockMvc.perform(post("/api/animais/{id}/microchip", animalId)
                        .session(session)
                        .contentType("application/json")
                        .content("""
                                {"identificador":"985112345678901"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativo").value(true));

        MvcResult vaccination = mockMvc.perform(post("/api/vacinas")
                        .session(session)
                        .contentType("application/json")
                        .content("""
                                {
                                  "animalId":%d,
                                  "nome":"Antirrabica de teste",
                                  "tipo":"ANTIRRABICA",
                                  "fabricante":"Laboratorio de Teste",
                                  "numeroLote":"LT-API-001",
                                  "dataAplicacao":"2026-01-15",
                                  "doseEtapa":"Dose anual",
                                  "viaAplicacao":"Subcutanea",
                                  "dataProximaDose":"2027-01-15",
                                  "dataValidadeLote":"2027-06-30",
                                  "situacao":"APLICADA"
                                }
                                """.formatted(animalId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.antirrabica").value(true))
                .andReturn();

        MvcResult failedIntegration = mockMvc.perform(get("/api/integracoes/animal/{id}", animalId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].situacao").value("FALHA"))
                .andExpect(jsonPath("$[0].quantidadeTentativas").value(1))
                .andReturn();
        long integrationId = json(failedIntegration).get(0).get("id").asLong();

        mockMvc.perform(post("/api/integracoes/{id}/tentativas", integrationId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("CONFIRMADA"))
                .andExpect(jsonPath("$.protocoloExterno").value("PROTOCOLO-TESTE-1"))
                .andExpect(jsonPath("$.quantidadeTentativas").value(2));
        var solicitacao = org.mockito.ArgumentCaptor.forClass(
                GovernoIntegrationClient.SolicitacaoVacinaAntirrabica.class);
        verify(governo, org.mockito.Mockito.times(2)).enviarVacinaAntirrabica(solicitacao.capture());
        org.junit.jupiter.api.Assertions.assertEquals("985112345678901",
                solicitacao.getAllValues().get(0).microchip());
        org.junit.jupiter.api.Assertions.assertEquals("90001", solicitacao.getAllValues().get(0).numeroCrmv());
        org.junit.jupiter.api.Assertions.assertEquals("RJ", solicitacao.getAllValues().get(0).ufCrmv());
        org.junit.jupiter.api.Assertions.assertEquals(solicitacao.getAllValues().get(0).idempotencyKey(),
                solicitacao.getAllValues().get(1).idempotencyKey());
        mockMvc.perform(get("/api/animais/{id}/vacinas", animalId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(json(vaccination).get("id").asLong()));
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
