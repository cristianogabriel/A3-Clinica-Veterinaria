package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.service.VeterinarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository;
    private final VeterinarioService veterinarios;

    public AutenticacaoController(AuthenticationManager authenticationManager,
                                  SecurityContextRepository contextRepository,
                                  VeterinarioService veterinarios) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
        this.veterinarios = veterinarios;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest,
                               HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim(), request.senha()));
        servletRequest.getSession(true);
        servletRequest.changeSessionId();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, servletRequest, response);
        veterinarios.registrarAcesso(authentication.getName());
        var vet = veterinarios.perfil(authentication.getName());
        return new LoginResponse(vet.id(), vet.nomeCompleto(), vet.email());
    }

    @PostMapping("/confirmacao")
    public ResponseEntity<Void> confirmar(@Valid @RequestBody ConfirmacaoRequest request) {
        veterinarios.confirmarSenha(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<VeterinarioCriadoResponse> cadastrarInicial(
            @RequestHeader("X-Bootstrap-Secret") String secret,
            @Valid @RequestBody VeterinarioRequest request) {
        VeterinarioCriadoResponse result = veterinarios.cadastrarInicial(secret, request);
        return ResponseEntity.created(java.net.URI.create("/api/veterinarios/" + result.id())).body(result);
    }

    @GetMapping("/me")
    public VeterinarioResponse perfil(Authentication authentication) {
        return veterinarios.perfil(authentication.getName());
    }
}
