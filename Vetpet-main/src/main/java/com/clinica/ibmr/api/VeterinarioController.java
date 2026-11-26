package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.service.VeterinarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {
    private final VeterinarioService service;
    public VeterinarioController(VeterinarioService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<VeterinarioCriadoResponse> cadastrar(@Valid @RequestBody VeterinarioRequest request) {
        VeterinarioCriadoResponse response = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + response.id())).body(response);
    }
    @GetMapping
    public List<VeterinarioResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public VeterinarioResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PutMapping("/{id}")
    public VeterinarioResponse atualizar(@PathVariable Long id,
            @Valid @RequestBody VeterinarioAtualizacaoRequest request) {
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}/situacao")
    public VeterinarioResponse alterarSituacao(@PathVariable Long id,
            @RequestParam ClinicaEnums.SituacaoCadastro situacao) {
        return service.alterarSituacao(id, situacao);
    }
}
