package com.clinica.ibmr.security;

import com.clinica.ibmr.domain.ClinicaEnums;
import com.clinica.ibmr.repository.VeterinarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioUserDetailsService implements UserDetailsService {
    private final VeterinarioRepository veterinarios;

    public VeterinarioUserDetailsService(VeterinarioRepository veterinarios) {
        this.veterinarios = veterinarios;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var veterinario = veterinarios.findByPessoa_EmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
        boolean habilitado = veterinario.getSituacao() == ClinicaEnums.SituacaoCadastro.ATIVO
                && veterinario.isSenhaConfirmada();
        return User.withUsername(veterinario.getPessoa().getEmail())
                .password(veterinario.getHashSenha())
                .roles("VETERINARIO")
                .disabled(!habilitado)
                .build();
    }
}
