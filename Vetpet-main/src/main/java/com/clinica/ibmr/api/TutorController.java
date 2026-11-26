package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.TutorRequest;
import com.clinica.ibmr.api.ApiDtos.TutorResponse;
import com.clinica.ibmr.api.ApiDtos.EnderecoRequest;
import com.clinica.ibmr.api.ApiDtos.EnderecoResponse;
import com.clinica.ibmr.service.TutorService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {
    private final TutorService service;
    public TutorController(TutorService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<TutorResponse> cadastrar(@Valid @RequestBody TutorRequest request) {
        TutorResponse result = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/tutores/" + result.id())).body(result);
    }
    @GetMapping
    public List<TutorResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public TutorResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PutMapping("/{id}")
    public TutorResponse atualizar(@PathVariable Long id, @Valid @RequestBody TutorRequest request) {
        return service.atualizar(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id); return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/endereco")
    public EnderecoResponse atualizarEndereco(@PathVariable Long id, @Valid @RequestBody EnderecoRequest request) {
        return service.atualizarEndereco(id, request);
    }
}
