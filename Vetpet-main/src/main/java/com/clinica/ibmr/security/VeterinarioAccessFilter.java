package com.clinica.ibmr.security;

import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.repository.VeterinarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class VeterinarioAccessFilter extends OncePerRequestFilter {
    private final VeterinarioRepository veterinarios;

    public VeterinarioAccessFilter(VeterinarioRepository veterinarios) {
        this.veterinarios = veterinarios;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/auth/login") || path.equals("/api/auth/confirmacao")
                || path.equals("/api/auth/bootstrap")
                || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html") || path.equals("/openapi.yaml");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
            boolean habilitado = veterinarios.findByPessoa_EmailIgnoreCase(user.getUsername())
                    .map(v -> v.getSituacao() == ClinicaEnums.SituacaoCadastro.ATIVO && v.isSenhaConfirmada())
                    .orElse(false);
            if (!habilitado) {
                SecurityContextHolder.clearContext();
                var session = request.getSession(false);
                if (session != null) session.invalidate();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Veterinário não autorizado.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
