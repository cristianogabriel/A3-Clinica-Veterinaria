package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.IntegracaoResponse;
import com.clinica.ibmr.service.IntegracaoService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integracoes")
public class IntegracaoController {
    private final IntegracaoService service;
    public IntegracaoController(IntegracaoService service) { this.service = service; }
    @GetMapping("/{id}")
    public IntegracaoResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping("/{id}/tentativas")
    public IntegracaoResponse tentarNovamente(@PathVariable Long id) { return service.tentarNovamente(id); }
    @GetMapping("/animal/{animalId}")
    public List<IntegracaoResponse> porAnimal(@PathVariable Long animalId) { return service.porAnimal(animalId); }
}
