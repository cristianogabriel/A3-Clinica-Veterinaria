package com.api.governo.controller;

import com.api.governo.dto.*;
import com.api.governo.entity.AnimalGov;
import com.api.governo.service.GovernoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/governo")
public class GovernoController {
    private final GovernoService service;
    public GovernoController(GovernoService service) { this.service=service; }
    @PostMapping("/animais") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> cadastrar(@Valid @RequestBody AnimalRequest request, @RequestHeader(value="Idempotency-Key",required=false) String chave) { return service.cadastrar(request,chave); }
    @GetMapping("/animais/{microchip}") public AnimalGov consultar(@PathVariable String microchip) { return service.consultarAnimal(microchip); }
    @PutMapping("/animais/{microchip}") public Map<String,Object> atualizar(@PathVariable String microchip,@Valid @RequestBody AnimalRequest request,@RequestHeader(value="Idempotency-Key",required=false) String chave) { return service.atualizar(microchip,request,chave); }
    @PostMapping("/vacinacoes-antirrabicas") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> vacinar(@Valid @RequestBody VacinacaoRequest request,@RequestHeader(value="Idempotency-Key",required=false) String chave) { return service.vacinar(request,chave); }
    @GetMapping("/operacoes/{protocolo}") public Map<String,Object> consultarOperacao(@PathVariable String protocolo) { return service.consultarOperacao(protocolo); }
}
