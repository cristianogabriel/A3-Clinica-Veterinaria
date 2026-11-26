package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.ViaCepResponse;
import com.clinica.ibmr.integration.ViaCepClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {
    private final ViaCepClient viaCep;
    public EnderecoController(ViaCepClient viaCep) { this.viaCep = viaCep; }

    @GetMapping("/cep/{cep}")
    public ViaCepResponse consultarCep(@PathVariable String cep) {
        if (!cep.matches("\\d{8}")) {
            throw new ApiExceptions.InvalidRequestException("CEP deve conter exatamente 8 dígitos.");
        }
        return viaCep.consultar(cep);
    }
}
