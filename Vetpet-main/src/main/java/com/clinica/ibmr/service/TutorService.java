package com.clinica.ibmr.service;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.api.ApiExceptions.*;
import com.clinica.ibmr.api.ApiMapper;
import com.clinica.ibmr.domain.*;
import com.clinica.ibmr.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TutorService {
    private final PessoaRepository pessoas;
    private final EnderecoRepository enderecos;
    private final TutorRepository tutores;

    public TutorService(PessoaRepository pessoas, EnderecoRepository enderecos, TutorRepository tutores) {
        this.pessoas = pessoas; this.enderecos = enderecos; this.tutores = tutores;
    }

    @Transactional
    public TutorResponse cadastrar(TutorRequest request) {
        String cpf = CpfValidator.normalizeAndValidate(request.cpf());
        validarPessoaUnica(cpf, request.email(), null);
        Pessoa pessoa = pessoas.save(new Pessoa(request.nomeCompleto().trim(), cpf, request.email().trim().toLowerCase(),
                request.telefone().trim()));
        Endereco endereco = enderecos.save(novoEndereco(request.endereco()));
        return ApiMapper.tutor(tutores.save(new Tutor(pessoa, endereco, request.dataNascimento())));
    }

    @Transactional(readOnly = true)
    public TutorResponse buscar(Long id) {
        return ApiMapper.tutor(tutores.findById(id).orElseThrow(() -> new NotFoundException("Tutor não encontrado.")));
    }

    @Transactional(readOnly = true)
    public List<TutorResponse> listar() {
        return tutores.findAll().stream().map(ApiMapper::tutor).toList();
    }

    @Transactional
    public TutorResponse atualizar(Long id, TutorRequest request) {
        Tutor tutor = tutores.findById(id).orElseThrow(() -> new NotFoundException("Tutor não encontrado."));
        Pessoa pessoa = tutor.getPessoa();
        String cpf = CpfValidator.normalizeAndValidate(request.cpf());
        validarPessoaUnica(cpf, request.email(), pessoa.getId());
        pessoa.setNomeCompleto(request.nomeCompleto().trim());
        pessoa.setCpf(cpf);
        pessoa.setEmail(request.email().trim().toLowerCase());
        pessoa.setTelefone(request.telefone().trim());
        tutor.setDataNascimento(request.dataNascimento());
        EnderecoRequest e = request.endereco();
        tutor.getEndereco().atualizar(e.cep(), e.logradouro().trim(), e.numero().trim(), e.complemento(),
                e.bairro().trim(), e.cidade().trim(), e.uf().toUpperCase(), e.codigoIbgeMunicipio());
        return ApiMapper.tutor(tutor);
    }

    @Transactional
    public void inativar(Long id) {
        Tutor tutor = tutores.findById(id).orElseThrow(() -> new NotFoundException("Tutor não encontrado."));
        tutor.inativar();
    }

    @Transactional
    public EnderecoResponse atualizarEndereco(Long id, EnderecoRequest request) {
        Tutor tutor = tutores.findById(id).orElseThrow(() -> new NotFoundException("Tutor não encontrado."));
        Endereco e = tutor.getEndereco();
        e.atualizar(request.cep(), request.logradouro().trim(), request.numero().trim(), request.complemento(),
                request.bairro().trim(), request.cidade().trim(), request.uf().toUpperCase(), request.codigoIbgeMunicipio());
        return ApiMapper.endereco(e);
    }

    private void validarPessoaUnica(String cpf, String email, Long pessoaAtual) {
        pessoas.findByEmailIgnoreCase(email.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(pessoaAtual)) throw new ConflictException("E-mail já cadastrado.");
        });
        if (pessoas.existsByCpf(cpf)) {
            boolean mesmoCpfAtual = pessoaAtual != null && pessoas.findById(pessoaAtual)
                    .map(p -> p.getCpf().equals(cpf)).orElse(false);
            if (!mesmoCpfAtual) throw new ConflictException("CPF já cadastrado.");
        }
    }

    private Endereco novoEndereco(EnderecoRequest e) {
        return new Endereco(e.cep(), e.logradouro().trim(), e.numero().trim(), e.complemento(),
                e.bairro().trim(), e.cidade().trim(), e.uf().toUpperCase(), e.codigoIbgeMunicipio());
    }
}
