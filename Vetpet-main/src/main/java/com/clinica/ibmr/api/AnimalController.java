package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.AnimalRequest;
import com.clinica.ibmr.api.ApiDtos.AnimalResponse;
import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.service.AnimalService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {
    private final AnimalService service;
    public AnimalController(AnimalService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<AnimalResponse> cadastrar(@Valid @RequestBody AnimalRequest request) {
        AnimalResponse result = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/animais/" + result.id())).body(result);
    }
    @GetMapping
    public List<AnimalResponse> listar(@RequestParam(required = false) Long tutorId) {
        return service.listar(tutorId);
    }
    @GetMapping("/{id}")
    public AnimalResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PutMapping("/{id}")
    public AnimalResponse atualizar(@PathVariable Long id, @Valid @RequestBody AnimalRequest request) {
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}/situacao")
    public AnimalResponse alterarSituacao(@PathVariable Long id, @RequestParam ClinicaEnums.SituacaoAnimal situacao) {
        return service.alterarSituacao(id, situacao);
    }
}
