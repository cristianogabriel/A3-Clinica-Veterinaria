package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.ConsultaRequest;
import com.clinica.ibmr.api.ApiDtos.ConsultaResponse;
import com.clinica.ibmr.api.ApiDtos.SituacaoConsultaRequest;
import com.clinica.ibmr.service.ConsultaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConsultaController {
    private final ConsultaService service;
    public ConsultaController(ConsultaService service) { this.service = service; }
    @PostMapping("/api/consultas")
    public ResponseEntity<ConsultaResponse> registrar(@Valid @RequestBody ConsultaRequest request,
                                                       Authentication authentication) {
        ConsultaResponse result = service.registrar(request, authentication.getName());
        return ResponseEntity.created(URI.create("/api/consultas/" + result.id())).body(result);
    }
    @PutMapping("/api/consultas/{id}")
    public ConsultaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaRequest request,
                                     Authentication authentication) {
        return service.atualizar(id, request, authentication.getName());
    }
    @PatchMapping("/api/consultas/{id}/situacao")
    public ConsultaResponse alterarSituacao(@PathVariable Long id,
            @Valid @RequestBody SituacaoConsultaRequest request) {
        return service.alterarSituacao(id, request.situacao());
    }
    @GetMapping({"/api/consultas/animal/{animalId}", "/api/animais/{animalId}/consultas"})
    public List<ConsultaResponse> historico(@PathVariable Long animalId) { return service.historico(animalId); }
}
