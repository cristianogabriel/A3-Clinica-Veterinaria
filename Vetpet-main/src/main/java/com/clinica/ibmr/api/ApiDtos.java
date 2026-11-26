package com.clinica.ibmr.api;

import com.clinica.ibmr.domain.ClinicaEnums;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ApiDtos {
    private ApiDtos() {}

    public record EnderecoRequest(
            @NotBlank @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos.") String cep,
            @NotBlank @Size(max = 150) String logradouro,
            @NotBlank @Size(max = 10) String numero,
            @Size(max = 80) String complemento,
            @NotBlank @Size(max = 80) String bairro,
            @NotBlank @Size(max = 80) String cidade,
            @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String uf,
            @Pattern(regexp = "\\d{7}") String codigoIbgeMunicipio) {}

    public record TutorRequest(
            @NotBlank @Size(max = 150) String nomeCompleto,
            @NotBlank @Size(max = 14) String cpf,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 20) String telefone,
            @NotNull @PastOrPresent LocalDate dataNascimento,
            @NotNull @Valid EnderecoRequest endereco) {}

    public record TutorResponse(Long id, String nomeCompleto, String cpf, String email, String telefone,
            LocalDate dataNascimento, LocalDateTime dataCadastro, ClinicaEnums.SituacaoCadastro situacao,
            EnderecoResponse endereco) {}

    public record EnderecoResponse(Long id, String cep, String logradouro, String numero, String complemento,
            String bairro, String cidade, String uf, String codigoIbgeMunicipio) {}

    public record ViaCepResponse(boolean encontrado, String cep, String logradouro, String complemento,
            String bairro, String cidade, String uf, String codigoIbgeMunicipio, String mensagem) {}

    public record EspecieRequest(@NotBlank @Size(max = 60) String nome, @Size(max = 255) String descricao) {}
    public record EspecieResponse(Long id, String nome, String descricao, ClinicaEnums.SituacaoCadastro situacao) {}
    public record RacaRequest(@NotNull Long especieId, @NotBlank @Size(max = 80) String nome,
                              @Size(max = 255) String descricao) {}
    public record RacaResponse(Long id, Long especieId, String nome, String descricao,
                               ClinicaEnums.SituacaoCadastro situacao) {}

    public record AnimalRequest(
            @NotNull Long tutorId,
            @NotNull Long especieId,
            Long racaId,
            @NotBlank @Size(max = 80) String nome,
            LocalDate dataNascimento,
            @PositiveOrZero @Max(32767) Integer idadeEstimada,
            @NotNull ClinicaEnums.Sexo sexo,
            @NotBlank @Size(max = 40) String cor,
            @Positive @Digits(integer = 4, fraction = 2) BigDecimal pesoAtual) {}
    public record AnimalResponse(Long id, Long tutorId, String nomeTutor, Long especieId, String especie,
            Long racaId, String raca, String nome, LocalDate dataNascimento, Integer idadeEstimada,
            ClinicaEnums.Sexo sexo, String cor, BigDecimal pesoAtual, ClinicaEnums.SituacaoAnimal situacao) {}

    public record MicrochipRequest(@NotBlank @Pattern(regexp = "[A-Za-z0-9]{1,20}") String identificador) {}
    public record MicrochipResponse(Long id, Long animalId, String identificador, Long veterinarioId,
            LocalDateTime dataVinculacao, LocalDateTime dataDesvinculacao, boolean ativo) {}

    public record ConsultaRequest(
            @NotNull Long animalId,
            @NotNull LocalDateTime dataHora,
            @NotBlank @Size(max = 255) String motivo,
            String anamnese, String sintomas, String diagnostico, String tratamentoPrescrito,
            String prescricaoMedicamentos,
            @Positive @Digits(integer = 4, fraction = 2) BigDecimal pesoRegistrado,
            @DecimalMin("20.0") @DecimalMax("50.0") @Digits(integer = 2, fraction = 1) BigDecimal temperatura,
            String observacoes,
            ClinicaEnums.SituacaoConsulta situacao) {}
    public record ConsultaResponse(Long id, Long animalId, Long veterinarioId, String nomeVeterinario,
            LocalDateTime dataHora, String motivo, String anamnese, String sintomas, String diagnostico,
            String tratamentoPrescrito, String prescricaoMedicamentos, BigDecimal pesoRegistrado,
            BigDecimal temperatura, String observacoes, ClinicaEnums.SituacaoConsulta situacao) {}
    public record SituacaoConsultaRequest(@NotNull ClinicaEnums.SituacaoConsulta situacao) {}

    public record VacinaRequest(
            @NotNull Long animalId,
            @NotBlank @Size(max = 100) String nome,
            @NotNull ClinicaEnums.TipoVacina tipo,
            @NotBlank @Size(max = 100) String fabricante,
            @NotBlank @Size(max = 40) String numeroLote,
            LocalDate dataAplicacao,
            @NotBlank @Size(max = 30) String doseEtapa,
            @NotBlank @Size(max = 30) String viaAplicacao,
            LocalDate dataProximaDose,
            @NotNull LocalDate dataValidadeLote,
            String observacao,
            ClinicaEnums.SituacaoVacina situacao) {}
    public record VacinaResponse(Long id, Long animalId, Long veterinarioId, String nome,
            ClinicaEnums.TipoVacina tipo, boolean antirrabica, String fabricante, String numeroLote,
            LocalDate dataAplicacao, String doseEtapa, String viaAplicacao, LocalDate dataProximaDose,
            LocalDate dataValidadeLote, String observacao, ClinicaEnums.SituacaoVacina situacao) {}
    public record SituacaoVacinaRequest(@NotNull ClinicaEnums.SituacaoVacina situacao, LocalDate dataAplicacao) {}

    public record IntegracaoResponse(Long id, Long animalId, Long vacinaId, Long vinculoMicrochipId,
            ClinicaEnums.TipoOperacaoIntegracao tipoOperacao, String protocoloExterno, String chaveIdempotencia,
            ClinicaEnums.SituacaoIntegracao situacao, int quantidadeTentativas,
            LocalDateTime dataHoraSolicitacao, LocalDateTime dataHoraResposta, String mensagemErro) {}
    public record ProximaDoseResponse(Long vacinaId, Long animalId, String nomeAnimal, String nomeVacina,
            LocalDate dataProximaDose, String doseEtapa) {}

    public record VeterinarioRequest(
            @NotBlank @Size(max = 150) String nomeCompleto,
            @NotBlank @Size(max = 14) String cpf,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 20) String telefone,
            @NotBlank @Size(max = 10) String numeroCrmv,
            @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String ufCrmv,
            @NotBlank @Size(min = 8, max = 72) String senha) {}
    public record VeterinarioAtualizacaoRequest(
            @NotBlank @Size(max = 150) String nomeCompleto,
            @NotBlank @Size(max = 14) String cpf,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 20) String telefone,
            @NotBlank @Size(max = 10) String numeroCrmv,
            @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String ufCrmv) {}
    public record VeterinarioCriadoResponse(Long id, String nomeCompleto, String email, String numeroCrmv,
            String ufCrmv, ClinicaEnums.SituacaoCadastro situacao, boolean senhaConfirmada,
            String codigoConfirmacao, LocalDateTime expiracaoCodigo) {}
    public record VeterinarioResponse(Long id, String nomeCompleto, String cpf, String email, String telefone,
            String numeroCrmv, String ufCrmv, ClinicaEnums.SituacaoCadastro situacao, boolean senhaConfirmada,
            LocalDateTime dataHoraConfirmacao, LocalDateTime dataHoraUltimoAcesso) {}
    public record ConfirmacaoRequest(@NotBlank @Email String email, @NotBlank String codigo) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {}
    public record LoginResponse(Long veterinarioId, String nomeCompleto, String email) {}
    public record MensagemResponse(String mensagem) {}
}
