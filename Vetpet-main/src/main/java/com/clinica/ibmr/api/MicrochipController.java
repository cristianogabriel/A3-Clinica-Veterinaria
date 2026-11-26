package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.MicrochipRequest;
import com.clinica.ibmr.api.ApiDtos.MicrochipResponse;
import com.clinica.ibmr.service.MicrochipService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animais/{animalId}/microchip")
public class MicrochipController {
    private final MicrochipService service;
    public MicrochipController(MicrochipService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<MicrochipResponse> vincular(@PathVariable Long animalId,
            @Valid @RequestBody MicrochipRequest request, Authentication authentication) {
        MicrochipResponse result = service.vincular(animalId, request.identificador(), authentication.getName());
        return ResponseEntity.created(URI.create("/api/animais/" + animalId + "/microchip/" + result.id())).body(result);
    }
    @GetMapping("/historico")
    public List<MicrochipResponse> historico(@PathVariable Long animalId) { return service.historico(animalId); }
}
