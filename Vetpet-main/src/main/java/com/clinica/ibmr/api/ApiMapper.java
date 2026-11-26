package com.clinica.ibmr.api;

import com.clinica.ibmr.api.ApiDtos.*;
import com.clinica.ibmr.domain.*;

public final class ApiMapper {
    private ApiMapper() {}

    public static EnderecoResponse endereco(Endereco e) {
        return new EnderecoResponse(e.getId(), e.getCep(), e.getLogradouro(), e.getNumero(), e.getComplemento(),
                e.getBairro(), e.getCidade(), e.getUf(), e.getCodigoIbgeMunicipio());
    }
    public static TutorResponse tutor(Tutor t) {
        Pessoa p = t.getPessoa();
        return new TutorResponse(t.getId(), p.getNomeCompleto(), p.getCpf(), p.getEmail(), p.getTelefone(),
                t.getDataNascimento(), p.getDataCadastro(), t.getSituacao(), endereco(t.getEndereco()));
    }
    public static EspecieResponse especie(Especie e) {
        return new EspecieResponse(e.getId(), e.getNome(), e.getDescricao(), e.getSituacao());
    }
    public static RacaResponse raca(Raca r) {
        return new RacaResponse(r.getId(), r.getEspecie().getId(), r.getNome(), r.getDescricao(), r.getSituacao());
    }
    public static AnimalResponse animal(Animal a) {
        return new AnimalResponse(a.getId(), a.getTutor().getId(), a.getTutor().getPessoa().getNomeCompleto(),
                a.getEspecie().getId(), a.getEspecie().getNome(),
                a.getRaca() == null ? null : a.getRaca().getId(),
                a.getRaca() == null ? null : a.getRaca().getNome(),
                a.getNome(), a.getDataNascimento(), a.getIdadeEstimada(), a.getSexo(), a.getCor(),
                a.getPesoAtual(), a.getSituacao());
    }
    public static MicrochipResponse microchip(VinculoMicrochip v) {
        return new MicrochipResponse(v.getId(), v.getAnimal().getId(), v.getIdentificador(),
                v.getVeterinario().getId(), v.getDataVinculacao(), v.getDataDesvinculacao(), v.isAtivo());
    }
    public static ConsultaResponse consulta(Consulta c) {
        return new ConsultaResponse(c.getId(), c.getAnimal().getId(), c.getVeterinario().getId(),
                c.getVeterinario().getPessoa().getNomeCompleto(), c.getDataHora(), c.getMotivo(),
                c.getAnamnese(), c.getSintomas(), c.getDiagnostico(), c.getTratamentoPrescrito(),
                c.getPrescricaoMedicamentos(), c.getPesoRegistrado(), c.getTemperatura(), c.getObservacoes(),
                c.getSituacao());
    }
    public static VacinaResponse vacina(Vacina v) {
        return new VacinaResponse(v.getId(), v.getAnimal().getId(), v.getVeterinario().getId(), v.getNome(),
                v.getTipo(), v.isAntirrabica(), v.getFabricante(), v.getNumeroLote(), v.getDataAplicacao(),
                v.getDoseEtapa(), v.getViaAplicacao(), v.getDataProximaDose(), v.getDataValidadeLote(),
                v.getObservacao(), v.getSituacao());
    }
    public static IntegracaoResponse integracao(IntegracaoGoverno i) {
        return new IntegracaoResponse(i.getId(), i.getAnimal().getId(),
                i.getVacina() == null ? null : i.getVacina().getId(),
                i.getVinculoMicrochip() == null ? null : i.getVinculoMicrochip().getId(),
                i.getTipoOperacao(), i.getProtocoloExterno(), i.getChaveIdempotencia(),
                i.getSituacao(), i.getQuantidadeTentativas(), i.getDataHoraSolicitacao(),
                i.getDataHoraResposta(), i.getMensagemErro());
    }
    public static VeterinarioResponse veterinario(Veterinario v) {
        Pessoa p = v.getPessoa();
        return new VeterinarioResponse(v.getId(), p.getNomeCompleto(), p.getCpf(), p.getEmail(), p.getTelefone(),
                v.getNumeroCrmv(), v.getUfCrmv(), v.getSituacao(), v.isSenhaConfirmada(),
                v.getDataHoraConfirmacao(), v.getDataHoraUltimoAcesso());
    }
}
