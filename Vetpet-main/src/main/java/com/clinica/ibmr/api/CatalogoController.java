package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.service.CatalogoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CatalogoController {
    private final CatalogoService service;
    public CatalogoController(CatalogoService service) { this.service = service; }

    @PostMapping("/api/especies")
    public ResponseEntity<EspecieResponse> cadastrarEspecie(@Valid @RequestBody EspecieRequest request) {
        EspecieResponse result = service.cadastrarEspecie(request);
        return ResponseEntity.created(URI.create("/api/especies/" + result.id())).body(result);
    }
    @GetMapping("/api/especies")
    public List<EspecieResponse> listarEspecies() { return service.listarEspecies(); }
    @GetMapping("/api/especies/{id}")
    public EspecieResponse buscarEspecie(@PathVariable Long id) { return service.buscarEspecie(id); }
    @PostMapping("/api/racas")
    public ResponseEntity<RacaResponse> cadastrarRaca(@Valid @RequestBody RacaRequest request) {
        RacaResponse result = service.cadastrarRaca(request);
        return ResponseEntity.created(URI.create("/api/racas/" + result.id())).body(result);
    }
    @GetMapping("/api/racas")
    public List<RacaResponse> listarRacas(@RequestParam(required = false) Long especieId) {
        return service.listarRacas(especieId);
    }
}
