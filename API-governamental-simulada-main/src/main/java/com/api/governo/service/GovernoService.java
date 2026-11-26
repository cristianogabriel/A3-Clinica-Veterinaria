package com.api.governo.service;

import com.api.governo.dto.*;
import com.api.governo.entity.*;
import com.api.governo.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GovernoService {
    private final AnimalGovRepository animais;
    private final OperacaoGovRepository operacoes;
    private final VacinacaoGovRepository vacinacoes;
    private final ObjectMapper mapper;
    @Value("${governo.simular-indisponibilidade:false}") private boolean indisponivel;
    public GovernoService(AnimalGovRepository a, OperacaoGovRepository o, VacinacaoGovRepository v, ObjectMapper m) { animais=a; operacoes=o; vacinacoes=v; mapper=m; }

    @Transactional
    public Map<String,Object> cadastrar(AnimalRequest req, String chave) {
        checarDisponibilidade(); validarChave(chave);
        OperacaoGov repetida = repeticao(chave, req);
        if (repetida != null) return respostaRepetida(repetida);
        if (animais.existsByCodigoMicrochip(req.codigoMicrochip())) throw erro(HttpStatus.CONFLICT,"Microchip já cadastrado.");
        AnimalGov animal = novoAnimal(req.codigoMicrochip(), req.nome(), req.dataNascimento(), req.sexo(), req.cor(), req.especie(), req.raca());
        animais.save(animal);
        Map<String,Object> resultado = new LinkedHashMap<>(); resultado.put("animal", animal); resultado.put("protocolo", "");
        OperacaoGov op = novaOperacao(chave,"CADASTRO_ANIMAL",req,resultado);
        resultado.put("protocolo",op.getProtocolo()); op.setResposta(json(resultado));
        operacoes.save(op); return resultado;
    }

    @Transactional(readOnly=true)
    public AnimalGov consultarAnimal(String microchip) { return animais.findByCodigoMicrochip(microchip).orElseThrow(() -> erro(HttpStatus.NOT_FOUND,"Animal não encontrado.")); }

    @Transactional
    public Map<String,Object> atualizar(String microchip, AnimalRequest req, String chave) {
        checarDisponibilidade(); validarChave(chave);
        AnimalGov animal = consultarAnimal(microchip);
        AnimalRequest normalizado = new AnimalRequest(microchip, req.nome(), req.dataNascimento(), req.sexo(), req.cor(), req.especie(), req.raca());
        OperacaoGov repetida = repeticao(chave, normalizado);
        if (repetida != null) return respostaRepetida(repetida);
        animal.setNome(req.nome()); animal.setDataNascimento(req.dataNascimento()); animal.setSexo(req.sexo()); animal.setCor(req.cor()); animal.setEspecie(req.especie()); animal.setRaca(req.raca()); animal.setDataAtualizacao(LocalDateTime.now());
        Map<String,Object> resultado = new LinkedHashMap<>(); resultado.put("animal", animal); resultado.put("protocolo", "");
        OperacaoGov op = novaOperacao(chave,"ATUALIZACAO_ANIMAL",normalizado,resultado); resultado.put("protocolo",op.getProtocolo()); op.setResposta(json(resultado)); operacoes.save(op); return resultado;
    }

    @Transactional
    public Map<String,Object> vacinar(VacinacaoRequest req, String chave) {
        checarDisponibilidade(); validarChave(chave);
        OperacaoGov repetida = repeticao(chave, req);
        if (repetida != null) return respostaRepetida(repetida);
        AnimalGov animal = animais.findByCodigoMicrochip(req.codigoMicrochip()).orElseGet(() -> {
            AnimalGov novo=novoAnimal(req.codigoMicrochip(),req.nomeAnimal(),req.dataNascimento(),req.sexo(),req.cor(),req.especie(),req.raca()); return animais.save(novo);
        });
        Map<String,Object> resultado = new LinkedHashMap<>(); resultado.put("codigoMicrochip",animal.getCodigoMicrochip()); resultado.put("idVacinacao", null); resultado.put("protocolo", "");
        OperacaoGov op = operacoes.saveAndFlush(novaOperacao(chave,"VACINACAO",req,resultado));
        VacinacaoGov v=new VacinacaoGov(); v.setAnimal(animal); v.setOperacao(op); v.setNomeVacina(req.nomeVacina()); v.setFabricante(req.fabricante()); v.setNumeroLote(req.numeroLote()); v.setDataAplicacao(req.dataAplicacao()); v.setDose(req.dose()); v.setViaAplicacao(req.viaAplicacao()); v.setValidadeLote(req.validadeLote()); v.setObservacoes(req.observacoes()); v.setCrmv(req.crmv()); v.setUfCrmv(req.ufCrmv()); v.setNomeVetResponsavel(req.nomeVetResponsavel());
        v = vacinacoes.saveAndFlush(v);
        resultado.put("idVacinacao", v.getId());
        resultado.put("protocolo",op.getProtocolo()); op.setResposta(json(resultado)); operacoes.save(op); return resultado;
    }

    @Transactional(readOnly=true)
    public Map<String,Object> consultarOperacao(String protocolo) {
        OperacaoGov op=operacoes.findByProtocolo(protocolo).orElseThrow(() -> erro(HttpStatus.NOT_FOUND,"Operação não encontrada."));
        Map<String,Object> result=new LinkedHashMap<>(); result.put("protocolo",op.getProtocolo()); result.put("tipoOperacao",op.getTipoOperacao()); result.put("situacao",op.getSituacao()); result.put("dataRecebimento",op.getDataRecebimento()); result.put("dataProcessamento",op.getDataProcessamento());
        try { result.put("resultado", mapper.readTree(op.getResposta())); } catch (Exception e) { result.put("resultado",op.getResposta()); }
        return result;
    }
    private AnimalGov novoAnimal(String codigo,String nome,java.time.LocalDate nascimento,String sexo,String cor,String especie,String raca) { AnimalGov a=new AnimalGov(); a.setCodigoMicrochip(codigo); a.setNome(nome); a.setDataNascimento(nascimento); a.setSexo(sexo); a.setCor(cor); a.setEspecie(especie); a.setRaca(raca); return a; }
    private OperacaoGov novaOperacao(String chave,String tipo,Object req,Object resultado) { OperacaoGov op=new OperacaoGov(); op.setChaveIdempotencia(chave); op.setTipoOperacao(tipo); op.setProtocolo(UUID.randomUUID().toString()); op.setSituacao("CONFIRMADA"); op.setDataProcessamento(LocalDateTime.now()); op.setRequisicao(json(req)); op.setResposta(json(resultado)); return op; }
    private OperacaoGov repeticao(String chave,Object req) { return operacoes.findByChaveIdempotencia(chave).map(op->{if(!op.getRequisicao().equals(json(req))) throw erro(HttpStatus.CONFLICT,"Chave de idempotência reutilizada com conteúdo diferente."); return op;}).orElse(null); }
    private Map<String,Object> respostaRepetida(OperacaoGov op) { try { return mapper.readValue(op.getResposta(),Map.class); } catch(Exception e) { throw erro(HttpStatus.INTERNAL_SERVER_ERROR,"Não foi possível recuperar a resposta da operação."); } }
    private String json(Object o) { try{return mapper.writeValueAsString(o);}catch(JsonProcessingException
     e){throw erro(HttpStatus.INTERNAL_SERVER_ERROR,"Falha ao processar os dados.");} }
    private void validarChave(String chave) { if(chave==null||chave.isBlank()||chave.length()>100) throw erro(HttpStatus.BAD_REQUEST,"Header Idempotency-Key obrigatório (até 100 caracteres)."); }
    private void checarDisponibilidade() { if(indisponivel) throw erro(HttpStatus.SERVICE_UNAVAILABLE,"Serviço governamental simulado indisponível."); }
    private ResponseStatusException erro(HttpStatus status,String msg) { return new ResponseStatusException(status,msg); }
}
