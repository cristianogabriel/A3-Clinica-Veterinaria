package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.repository.PessoaRepository;
import com.clinica.ibmr.repository.VeterinarioRepository;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeterinarioService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PessoaRepository pessoas;
    private final VeterinarioRepository veterinarios;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${app.bootstrap.secret:}")
    private String bootstrapSecret;

    public VeterinarioService(PessoaRepository pessoas, VeterinarioRepository veterinarios,
                              PasswordEncoder passwordEncoder, EmailService emailService) {
        this.pessoas = pessoas; this.veterinarios = veterinarios; this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public VeterinarioCriadoResponse cadastrar(VeterinarioRequest request) {
        String cpf = CpfValidator.normalizeAndValidate(request.cpf());
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (pessoas.existsByCpf(cpf)) throw new ConflictException("CPF já cadastrado.");
        if (pessoas.existsByEmailIgnoreCase(email)) throw new ConflictException("E-mail já cadastrado.");
        String uf = request.ufCrmv().toUpperCase(Locale.ROOT);
        if (veterinarios.existsByNumeroCrmvAndUfCrmv(request.numeroCrmv().trim(), uf)) {
            throw new ConflictException("CRMV já cadastrado para esta UF.");
        }
        Pessoa pessoa = pessoas.save(new Pessoa(request.nomeCompleto().trim(), cpf, email, request.telefone().trim()));
        Veterinario vet = new Veterinario(pessoa, request.numeroCrmv().trim(), uf,
                passwordEncoder.encode(request.senha()));
        String code = "%06d".formatted(RANDOM.nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(20);
        vet.definirConfirmacao(passwordEncoder.encode(code), expiry);
        veterinarios.save(vet);
        boolean enviado = emailService.enviarCodigoConfirmacao(email, pessoa.getNomeCompleto(), code);
        return new VeterinarioCriadoResponse(pessoa.getId(), pessoa.getNomeCompleto(), pessoa.getEmail(),
                vet.getNumeroCrmv(), vet.getUfCrmv(), vet.getSituacao(), false, enviado ? null : code, expiry);
    }

    @Transactional
    public VeterinarioCriadoResponse cadastrarInicial(String secret, VeterinarioRequest request) {
        if (bootstrapSecret == null || bootstrapSecret.isBlank()) {
            throw new ConflictException("Cadastro inicial indisponível: configure BOOTSTRAP_SECRET.");
        }
        if (secret == null || !MessageDigest.isEqual(
                bootstrapSecret.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8))) {
            throw new ConflictException("Chave de cadastro inicial inválida.");
        }
        boolean alreadyProvisioned = veterinarios.findAll().stream()
                .anyMatch(v -> v.getHashSenha().matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$"));
        if (alreadyProvisioned) {
            throw new ConflictException("O cadastro inicial já foi utilizado.");
        }
        return cadastrar(request);
    }

    @Transactional
    public void confirmarSenha(ConfirmacaoRequest request) {
        Veterinario vet = veterinarios.findByPessoa_EmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new InvalidRequestException("Código de confirmação inválido ou expirado."));
        if (vet.getCodigoConfirmacaoHash() == null || vet.getDataHoraExpiracaoCodigo() == null
                || vet.getDataHoraExpiracaoCodigo().isBefore(LocalDateTime.now())
                || !passwordEncoder.matches(request.codigo(), vet.getCodigoConfirmacaoHash())) {
            throw new InvalidRequestException("Código de confirmação inválido ou expirado.");
        }
        vet.confirmarSenha(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<VeterinarioResponse> listar() {
        return veterinarios.findAll().stream().map(ApiMapper::veterinario).toList();
    }

    @Transactional(readOnly = true)
    public VeterinarioResponse buscar(Long id) {
        return ApiMapper.veterinario(obter(id));
    }

    @Transactional
    public VeterinarioResponse atualizar(Long id, VeterinarioAtualizacaoRequest request) {
        Veterinario vet = obter(id);
        Pessoa pessoa = vet.getPessoa();
        String cpf = CpfValidator.normalizeAndValidate(request.cpf());
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        pessoas.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (!existing.getId().equals(pessoa.getId())) throw new ConflictException("E-mail já cadastrado.");
        });
        if (pessoas.existsByCpf(cpf) && !pessoa.getCpf().equals(cpf)) {
            throw new ConflictException("CPF já cadastrado.");
        }
        String numeroCrmv = request.numeroCrmv().trim();
        String ufCrmv = request.ufCrmv().toUpperCase(Locale.ROOT);
        boolean crmvAlterado = !vet.getNumeroCrmv().equals(numeroCrmv) || !vet.getUfCrmv().equals(ufCrmv);
        if (crmvAlterado && veterinarios.existsByNumeroCrmvAndUfCrmv(numeroCrmv, ufCrmv)) {
            throw new ConflictException("CRMV já cadastrado para esta UF.");
        }
        pessoa.setNomeCompleto(request.nomeCompleto().trim());
        pessoa.setCpf(cpf);
        pessoa.setEmail(email);
        pessoa.setTelefone(request.telefone().trim());
        vet.atualizarDadosProfissionais(numeroCrmv, ufCrmv);
        return ApiMapper.veterinario(vet);
    }

    @Transactional(readOnly = true)
    public VeterinarioResponse perfil(String email) {
        return ApiMapper.veterinario(veterinarios.findByPessoa_EmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Veterinário não encontrado.")));
    }

    @Transactional
    public void registrarAcesso(String email) {
        Veterinario vet = veterinarios.findByPessoa_EmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Veterinário não encontrado."));
        vet.registrarAcesso(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Veterinario obterAtivo(String email) {
        Veterinario vet = veterinarios.findByPessoa_EmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Veterinário não encontrado."));
        if (vet.getSituacao() != ClinicaEnums.SituacaoCadastro.ATIVO || !vet.isSenhaConfirmada()) {
            throw new InvalidRequestException("Veterinário inativo ou não confirmado.");
        }
        return vet;
    }

    @Transactional
    public VeterinarioResponse alterarSituacao(Long id, ClinicaEnums.SituacaoCadastro situacao) {
        Veterinario vet = obter(id);
        vet.alterarSituacao(situacao);
        return ApiMapper.veterinario(vet);
    }

    private Veterinario obter(Long id) {
        return veterinarios.findById(id).orElseThrow(() -> new NotFoundException("Veterinário não encontrado."));
    }
}
