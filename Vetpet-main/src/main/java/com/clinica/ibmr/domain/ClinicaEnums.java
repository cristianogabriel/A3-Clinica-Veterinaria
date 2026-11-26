package com.clinica.ibmr.domain;

public final class ClinicaEnums {
    private ClinicaEnums() {}

    public enum SituacaoCadastro { ATIVO, INATIVO }
    public enum SituacaoAnimal { ATIVO, INATIVO, OBITO }
    public enum Sexo { M, F }
    public enum SituacaoConsulta { AGENDADA, REALIZADA, CANCELADA, NAO_COMPARECEU }
    public enum TipoVacina { ANTIRRABICA, POLIVALENTE, OUTRA }
    public enum SituacaoVacina { AGENDADA, APLICADA, CANCELADA }
    public enum SituacaoIntegracao { PENDENTE, ENVIADA, CONFIRMADA, FALHA }
    public enum TipoOperacaoIntegracao { CADASTRO_ANIMAL, ATUALIZACAO_ANIMAL, VACINA_ANTIRRABICA }
}
