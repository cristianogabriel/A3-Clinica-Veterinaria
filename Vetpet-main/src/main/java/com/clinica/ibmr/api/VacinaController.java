package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.service.VacinaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class VacinaController {
    private final VacinaService service;
    public VacinaController(VacinaService service) { this.service = service; }
    @PostMapping("/api/vacinas")
    public ResponseEntity<VacinaResponse> registrar(@Valid @RequestBody VacinaRequest request,
                                                     Authentication authentication) {
        VacinaResponse result = service.registrar(request, authentication.getName());
        return ResponseEntity.created(URI.create("/api/vacinas/" + result.id())).body(result);
    }
    @PatchMapping("/api/vacinas/{id}/situacao")
    public VacinaResponse alterarSituacao(@PathVariable Long id,
            @Valid @RequestBody SituacaoVacinaRequest request) {
        return service.alterarSituacao(id, request);
    }
    @GetMapping("/api/animais/{animalId}/vacinas")
    public List<VacinaResponse> historico(@PathVariable Long animalId) { return service.historico(animalId); }
    @GetMapping("/api/vacinas/proximas")
    public List<ProximaDoseResponse> proximas(
            @RequestParam(required = false) Long animalId,
            @RequestParam(defaultValue = "30") int dias) {
        return service.proximas(animalId, dias);
    }
}
