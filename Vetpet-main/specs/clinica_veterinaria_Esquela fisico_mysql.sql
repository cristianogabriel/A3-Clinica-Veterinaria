USE clinica_veterinaria;
SET NAMES utf8mb4;

CREATE TABLE Enderecos (
    ID                    BIGINT       NOT NULL AUTO_INCREMENT,
    CEP                   CHAR(8)      NOT NULL,
    Logradouro            VARCHAR(150) NOT NULL,
    Numero                VARCHAR(10)  NOT NULL,
    Complemento           VARCHAR(80)  NULL,
    Bairro                VARCHAR(80)  NOT NULL,
    Cidade                VARCHAR(80)  NOT NULL,
    UF                    CHAR(2)      NOT NULL,
    Codigo_IBGE_Municipio CHAR(7)      NULL,
    PRIMARY KEY (ID),
    CONSTRAINT ck_enderecos_cep CHECK (CEP REGEXP '^[0-9]{8}$')
) ENGINE=InnoDB;


CREATE TABLE Pessoas (
    ID            BIGINT       NOT NULL AUTO_INCREMENT,
    Nome_Completo VARCHAR(150) NOT NULL,
    CPF           CHAR(11)     NOT NULL,
    Email         VARCHAR(150) NOT NULL,
    Telefone      VARCHAR(20)  NOT NULL,
    Data_Cadastro DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    CONSTRAINT uq_pessoas_cpf   UNIQUE (CPF),
    CONSTRAINT uq_pessoas_email UNIQUE (Email),
    CONSTRAINT ck_pessoas_cpf   CHECK (CPF REGEXP '^[0-9]{11}$')
) ENGINE=InnoDB;

CREATE TABLE Tutores (
    fk_Pessoas_ID     BIGINT      NOT NULL,
    fk_Enderecos_ID   BIGINT      NOT NULL,
    Data_Nascimento   DATE        NOT NULL,
    Situacao_Cadastro VARCHAR(10) NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (fk_Pessoas_ID),
    CONSTRAINT fk_tutores_pessoa   FOREIGN KEY (fk_Pessoas_ID)   REFERENCES Pessoas (ID)  ,
    CONSTRAINT fk_tutores_endereco FOREIGN KEY (fk_Enderecos_ID) REFERENCES Enderecos (ID),
    CONSTRAINT ck_tutores_situacao CHECK (Situacao_Cadastro IN ('ATIVO','INATIVO'))
) ENGINE=InnoDB;

CREATE TABLE Veterinarios (
    fk_Pessoas_ID              BIGINT       NOT NULL,
    Numero_CRMV                VARCHAR(10)  NOT NULL,
    UF_CRMV                    CHAR(2)      NOT NULL,
    Hash_Senha                 VARCHAR(255) NOT NULL,
    Senha_Confirmada           BOOLEAN      NOT NULL DEFAULT FALSE,
    Data_Hora_Confirmacao      DATETIME     NULL,
    Codigo_Confirmacao_Hash    VARCHAR(255) NULL,
    Data_Hora_Expiracao_Codigo DATETIME     NULL,
    Situacao_Veterinario       VARCHAR(10)  NOT NULL DEFAULT 'ATIVO',
    Data_Hora_Ultimo_Acesso    DATETIME     NULL,
    PRIMARY KEY (fk_Pessoas_ID),
    CONSTRAINT fk_veterinarios_pessoa FOREIGN KEY (fk_Pessoas_ID) REFERENCES Pessoas (ID),
    CONSTRAINT uq_veterinarios_crmv UNIQUE (Numero_CRMV, UF_CRMV),
    CONSTRAINT ck_veterinarios_situacao CHECK (Situacao_Veterinario IN ('ATIVO','INATIVO')),
    CONSTRAINT ck_veterinarios_confirmacao
        CHECK (Senha_Confirmada = (Data_Hora_Confirmacao IS NOT NULL))
) ENGINE=InnoDB;

CREATE TABLE Especies (
    ID        BIGINT       NOT NULL AUTO_INCREMENT,
    Nome      VARCHAR(60)  NOT NULL,
    Descricao VARCHAR(255) NULL,
    Situacao  VARCHAR(10)  NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (ID),
    CONSTRAINT uq_especies_nome UNIQUE (Nome),
    CONSTRAINT ck_especies_situacao CHECK (Situacao IN ('ATIVO','INATIVO'))
) ENGINE=InnoDB;

CREATE TABLE Racas (
    ID             BIGINT       NOT NULL AUTO_INCREMENT,
    fk_Especies_ID BIGINT       NOT NULL,
    Nome           VARCHAR(80)  NOT NULL,
    Descricao      VARCHAR(255) NULL,
    Situacao       VARCHAR(10)  NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (ID),
    CONSTRAINT fk_racas_especie FOREIGN KEY (fk_Especies_ID) REFERENCES Especies (ID),
    CONSTRAINT uq_racas_especie_nome UNIQUE (fk_Especies_ID, Nome),
    -- alvo da FK composta de Animais (raça compatível com a espécie)
    CONSTRAINT uq_racas_id_especie UNIQUE (ID, fk_Especies_ID),
    CONSTRAINT ck_racas_situacao CHECK (Situacao IN ('ATIVO','INATIVO'))
) ENGINE=InnoDB;

CREATE TABLE Animais (
    ID_Interno               BIGINT       NOT NULL AUTO_INCREMENT,
    fk_Especies_ID           BIGINT       NOT NULL,
    fk_Racas_ID              BIGINT       NULL,       -- NULL = sem raça definida
    fk_Tutores_fk_Pessoas_ID BIGINT       NOT NULL,
    Nome                     VARCHAR(80)  NOT NULL,
    Data_nascimento          DATE         NULL,
    Idade_Estimada           SMALLINT     NULL,
    Sexo                     CHAR(1)      NOT NULL,
    Cor                      VARCHAR(40)  NOT NULL,
    Peso_Atual               DECIMAL(6,2) NULL,
    Situacao_animal          VARCHAR(10)  NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (ID_Interno),
    CONSTRAINT fk_animais_especie FOREIGN KEY (fk_Especies_ID) REFERENCES Especies (ID),
    CONSTRAINT fk_animais_tutor   FOREIGN KEY (fk_Tutores_fk_Pessoas_ID) REFERENCES Tutores (fk_Pessoas_ID),
    -- Raça compatível com a espécie. Com fk_Racas_ID NULL a FK não é checada.
    CONSTRAINT fk_animais_raca_especie FOREIGN KEY (fk_Racas_ID, fk_Especies_ID)
        REFERENCES Racas (ID, fk_Especies_ID),
    CONSTRAINT ck_animais_sexo CHECK (Sexo IN ('M','F')),
    CONSTRAINT ck_animais_idade CHECK (Idade_Estimada IS NULL OR Idade_Estimada >= 0),
    CONSTRAINT ck_animais_peso CHECK (Peso_Atual IS NULL OR Peso_Atual > 0),
    CONSTRAINT ck_animais_nascimento CHECK (Data_nascimento IS NOT NULL OR Idade_Estimada IS NOT NULL),
    CONSTRAINT ck_animais_situacao CHECK (Situacao_animal IN ('ATIVO','INATIVO','OBITO'))
) ENGINE=InnoDB;

CREATE TABLE Vinculo_microchip (
    ID_Vinculo                    BIGINT      NOT NULL AUTO_INCREMENT,
    fk_Veterinarios_fk_Pessoas_ID BIGINT      NOT NULL,
    fk_Animais_ID_Interno         BIGINT      NOT NULL,
    Data_Vinculacao_Microchip     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ID_Microchip_RFID             VARCHAR(20) NOT NULL,
    Data_Desvinculacao            DATETIME    NULL,   -- NULL = vínculo ativo
    -- Coluna gerada: o MySQL não tem índice único parcial. Como UNIQUE aceita
    -- vários NULL, só vínculos ativos entram na restrição (1 chip ativo por animal).
    Animal_Vinculo_Ativo BIGINT GENERATED ALWAYS AS
        (CASE WHEN Data_Desvinculacao IS NULL THEN fk_Animais_ID_Interno END) STORED,
    PRIMARY KEY (ID_Vinculo),
    CONSTRAINT fk_vinculo_veterinario FOREIGN KEY (fk_Veterinarios_fk_Pessoas_ID)
        REFERENCES Veterinarios (fk_Pessoas_ID),
    CONSTRAINT fk_vinculo_animal FOREIGN KEY (fk_Animais_ID_Interno)
        REFERENCES Animais (ID_Interno),
    CONSTRAINT uq_vinculo_microchip UNIQUE (ID_Microchip_RFID),
    CONSTRAINT uq_vinculo_ativo UNIQUE (Animal_Vinculo_Ativo),
    CONSTRAINT ck_vinculo_datas CHECK
        (Data_Desvinculacao IS NULL OR Data_Desvinculacao >= Data_Vinculacao_Microchip)
) ENGINE=InnoDB;

CREATE TABLE Consultas (
    ID                            BIGINT       NOT NULL AUTO_INCREMENT,
    fk_Animais_ID_Interno         BIGINT       NOT NULL,
    fk_Veterinarios_fk_Pessoas_ID BIGINT       NOT NULL,
    Data_Hora_Consulta            DATETIME     NOT NULL,
    Motivo_Atendimento            VARCHAR(255) NOT NULL,
    Anamnese                      TEXT         NULL,
    Sintomas                      TEXT         NULL,
    Diagnostico                   TEXT         NULL,
    Tratamento_Prescrito          TEXT         NULL,
    Prescricao_Medicamentos       TEXT         NULL,
    Peso_Registrado               DECIMAL(6,2) NULL,
    Temperatura                   DECIMAL(4,1) NULL,
    Observacoes                   TEXT         NULL,
    Situacao                      VARCHAR(15)  NOT NULL DEFAULT 'AGENDADA',
    PRIMARY KEY (ID),
    CONSTRAINT fk_consultas_animal FOREIGN KEY (fk_Animais_ID_Interno)
        REFERENCES Animais (ID_Interno),
    CONSTRAINT fk_consultas_veterinario FOREIGN KEY (fk_Veterinarios_fk_Pessoas_ID)
        REFERENCES Veterinarios (fk_Pessoas_ID),
    CONSTRAINT ck_consultas_situacao CHECK (Situacao IN ('AGENDADA','REALIZADA','CANCELADA','NAO_COMPARECEU')),
    CONSTRAINT ck_consultas_peso CHECK (Peso_Registrado IS NULL OR Peso_Registrado > 0),
    CONSTRAINT ck_consultas_realizada CHECK
        (Situacao <> 'REALIZADA' OR (Diagnostico IS NOT NULL AND Tratamento_Prescrito IS NOT NULL)),
    INDEX ix_consultas_historico (fk_Animais_ID_Interno, Data_Hora_Consulta)
) ENGINE=InnoDB;

CREATE TABLE Vacinas (
    ID_Aplicacao                  BIGINT       NOT NULL AUTO_INCREMENT,
    fk_Animais_ID_Interno         BIGINT       NOT NULL,
    fk_Veterinarios_fk_Pessoas_ID BIGINT       NOT NULL,
    Nome_Vacina                   VARCHAR(100) NOT NULL,
    Tipo_Vacina                   VARCHAR(20)  NOT NULL,
    Eh_Antirrabica                BOOLEAN      NOT NULL DEFAULT FALSE,
    Fabricante                    VARCHAR(100) NOT NULL,
    Numero_Lote                   VARCHAR(40)  NOT NULL,
    Data_aplicacao                DATE         NULL,   -- NULL enquanto agendada
    Dose_Etapa_Esquema            VARCHAR(30)  NOT NULL,
    Via_Aplicacao                 VARCHAR(30)  NOT NULL,
    Data_Proxima_Dose             DATE         NULL,
    Data_Validade_Lote            DATE         NOT NULL,
    Observacao                    TEXT         NULL,
    Situacao_Aplicacao            VARCHAR(10)  NOT NULL DEFAULT 'AGENDADA',
    PRIMARY KEY (ID_Aplicacao),
    CONSTRAINT fk_vacinas_animal FOREIGN KEY (fk_Animais_ID_Interno)
        REFERENCES Animais (ID_Interno),
    CONSTRAINT fk_vacinas_veterinario FOREIGN KEY (fk_Veterinarios_fk_Pessoas_ID)
        REFERENCES Veterinarios (fk_Pessoas_ID),
    CONSTRAINT ck_vacinas_tipo CHECK (Tipo_Vacina IN ('ANTIRRABICA','POLIVALENTE','OUTRA')),
    CONSTRAINT ck_vacinas_antirrabica CHECK (Eh_Antirrabica = (Tipo_Vacina = 'ANTIRRABICA')),
    CONSTRAINT ck_vacinas_situacao CHECK (Situacao_Aplicacao IN ('AGENDADA','APLICADA','CANCELADA')),
    CONSTRAINT ck_vacinas_aplicada CHECK (Situacao_Aplicacao <> 'APLICADA' OR Data_aplicacao IS NOT NULL),
    CONSTRAINT ck_vacinas_validade CHECK (Data_aplicacao IS NULL OR Data_aplicacao <= Data_Validade_Lote),
    CONSTRAINT ck_vacinas_proxima CHECK
        (Data_Proxima_Dose IS NULL OR Data_aplicacao IS NULL OR Data_Proxima_Dose > Data_aplicacao),
    INDEX ix_vacinas_historico (fk_Animais_ID_Interno, Data_aplicacao)
) ENGINE=InnoDB;

CREATE TABLE IntegracaoGoverno (
    ID_Integracao                   BIGINT       NOT NULL AUTO_INCREMENT,
    fk_Animais_ID_Interno           BIGINT       NOT NULL,
    fk_Vacinas_ID_Aplicacao         BIGINT       NULL,   -- só em operações de vacina
    fk_Vinculo_microchip_ID_Vinculo BIGINT       NULL,   -- chip efetivamente enviado
    Tipo_Operacao                   VARCHAR(25)  NOT NULL,
    Protocolo_Externo               VARCHAR(40)  NULL,
    Chave_Idempotencia              VARCHAR(100) NOT NULL,
    Situacao_Integracao             VARCHAR(12)  NOT NULL DEFAULT 'PENDENTE',
    Quantidade_Tentativas           INT          NOT NULL DEFAULT 0,
    Data_Hora_Solicitacao           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Data_Hora_Resposta              DATETIME     NULL,
    Mensagem_Erro                   TEXT         NULL,
    PRIMARY KEY (ID_Integracao),
    CONSTRAINT fk_integracao_animal FOREIGN KEY (fk_Animais_ID_Interno)
        REFERENCES Animais (ID_Interno),
    CONSTRAINT fk_integracao_vacina FOREIGN KEY (fk_Vacinas_ID_Aplicacao)
        REFERENCES Vacinas (ID_Aplicacao),
    CONSTRAINT fk_integracao_vinculo FOREIGN KEY (fk_Vinculo_microchip_ID_Vinculo)
        REFERENCES Vinculo_microchip (ID_Vinculo),
    CONSTRAINT uq_integracao_vacina UNIQUE (fk_Vacinas_ID_Aplicacao),
    CONSTRAINT uq_integracao_protocolo UNIQUE (Protocolo_Externo),
    CONSTRAINT uq_integracao_idempotencia UNIQUE (Chave_Idempotencia),
    CONSTRAINT ck_integracao_tipo CHECK
        (Tipo_Operacao IN ('CADASTRO_ANIMAL','ATUALIZACAO_ANIMAL','VACINA_ANTIRRABICA')),
    CONSTRAINT ck_integracao_situacao CHECK
        (Situacao_Integracao IN ('PENDENTE','ENVIADA','CONFIRMADA','FALHA')),
    CONSTRAINT ck_integracao_tentativas CHECK (Quantidade_Tentativas >= 0),
    CONSTRAINT ck_integracao_vacina_obrig CHECK
        (Tipo_Operacao <> 'VACINA_ANTIRRABICA' OR fk_Vacinas_ID_Aplicacao IS NOT NULL),
    CONSTRAINT ck_integracao_confirmada CHECK
        (Situacao_Integracao <> 'CONFIRMADA' OR Protocolo_Externo IS NOT NULL)
) ENGINE=InnoDB;

INSERT INTO Enderecos (ID, CEP, Logradouro, Numero, Complemento, Bairro, Cidade, UF, Codigo_IBGE_Municipio) VALUES
(1, '20040020', 'Rua Exemplo', '100', 'Apto 101', 'Centro',  'Rio de Janeiro', 'RJ', '3304557'),
(2, '24020005', 'Avenida Teste', '250', NULL,      'Centro',  'Niterói',        'RJ', '3303302');

INSERT INTO Pessoas (ID, Nome_Completo, CPF, Email, Telefone) VALUES
(1, 'Ana Souza (Veterinária ativa)',            '11144477735', 'ana.souza@clinica.exemplo',   '21990000001'),
(2, 'Bruno Lima (Veterinário inativo)',         '52998224725', 'bruno.lima@clinica.exemplo',  '21990000002'),
(3, 'Carla Mendes (Veterinária sem confirmação)','39053344705', 'carla.mendes@clinica.exemplo','21990000003'),
(4, 'Diego Ramos (Tutor)',                      '16899535009', 'diego.ramos@email.exemplo',   '21990000004'),
(5, 'Elisa Prado (Tutora)',                     '45317828791', 'elisa.prado@email.exemplo',   '21990000005');

INSERT INTO Veterinarios
(fk_Pessoas_ID, Numero_CRMV, UF_CRMV, Hash_Senha, Senha_Confirmada, Data_Hora_Confirmacao, Situacao_Veterinario) VALUES
(1, '10001', 'RJ', '$2b$12$PLACEHOLDER_SUBSTITUA_PELO_HASH_REAL_ANA',   TRUE,  '2026-01-10 09:00:00', 'ATIVO'),
(2, '10002', 'RJ', '$2b$12$PLACEHOLDER_SUBSTITUA_PELO_HASH_REAL_BRUNO', TRUE,  '2026-01-11 09:00:00', 'INATIVO'),
(3, '10003', 'RJ', '$2b$12$PLACEHOLDER_SUBSTITUA_PELO_HASH_REAL_CARLA', FALSE, NULL,                  'ATIVO');

INSERT INTO Tutores (fk_Pessoas_ID, fk_Enderecos_ID, Data_Nascimento, Situacao_Cadastro) VALUES
(4, 1, '1988-04-12', 'ATIVO'),
(5, 2, '1992-09-30', 'ATIVO');

INSERT INTO Especies (ID, Nome, Descricao) VALUES
(1, 'Cão',  'Canis lupus familiaris'),
(2, 'Gato', 'Felis catus'),
(3, 'Ave',  'Aves domésticas');

INSERT INTO Racas (ID, fk_Especies_ID, Nome) VALUES
(1, 1, 'Labrador Retriever'),
(2, 1, 'Poodle'),
(3, 2, 'Siamês');

INSERT INTO Animais
(ID_Interno, fk_Especies_ID, fk_Racas_ID, fk_Tutores_fk_Pessoas_ID, Nome, Data_nascimento, Idade_Estimada, Sexo, Cor, Peso_Atual) VALUES
(1, 1, 1,    4, 'Thor', '2021-03-15', NULL, 'M', 'Caramelo', 28.50),
(2, 2, NULL, 5, 'Mia',  NULL,         24,   'F', 'Cinza',     4.20);

INSERT INTO Vinculo_microchip
(ID_Vinculo, fk_Veterinarios_fk_Pessoas_ID, fk_Animais_ID_Interno, Data_Vinculacao_Microchip, ID_Microchip_RFID) VALUES
(1, 1, 1, '2026-02-01 10:00:00', '985112345678901'),
(2, 1, 2, '2026-02-01 10:30:00', '985112345678902');

INSERT INTO Consultas
(ID, fk_Animais_ID_Interno, fk_Veterinarios_fk_Pessoas_ID, Data_Hora_Consulta, Motivo_Atendimento,
 Anamnese, Sintomas, Diagnostico, Tratamento_Prescrito, Peso_Registrado, Temperatura, Situacao) VALUES
(1, 1, 1, '2026-02-01 10:00:00', 'Check-up anual',
 'Tutor relata animal ativo e sem queixas.', 'Nenhum', 'Animal saudável', 'Manter rotina e vacinação em dia', 28.50, 38.5, 'REALIZADA'),
(2, 2, 1, '2026-12-10 14:00:00', 'Retorno', NULL, NULL, NULL, NULL, NULL, NULL, 'AGENDADA');

INSERT INTO Vacinas
(ID_Aplicacao, fk_Animais_ID_Interno, fk_Veterinarios_fk_Pessoas_ID, Nome_Vacina, Tipo_Vacina, Eh_Antirrabica,
 Fabricante, Numero_Lote, Data_aplicacao, Dose_Etapa_Esquema, Via_Aplicacao, Data_Proxima_Dose, Data_Validade_Lote, Situacao_Aplicacao) VALUES
(1, 1, 1, 'Antirrábica Canina', 'ANTIRRABICA', TRUE,  'Laboratório Exemplo', 'LT-RAIVA-001', '2026-02-01', 'Dose anual',  'Subcutânea', '2027-02-01', '2027-06-30', 'APLICADA'),
(2, 2, 1, 'Polivalente Felina', 'POLIVALENTE', FALSE, 'Laboratório Exemplo', 'LT-POLI-014',  '2026-02-01', 'Reforço 1',   'Subcutânea', '2027-02-01', '2027-03-31', 'APLICADA');

-- Cadastro do Thor já confirmado; antirrábica com comunicação PENDENTE
-- (cenário dos testes 14 e 15: indisponibilidade e reenvio). A vacina
-- não antirrábica (ID 2) não gera integração (teste 13).
INSERT INTO IntegracaoGoverno
(ID_Integracao, fk_Animais_ID_Interno, fk_Vacinas_ID_Aplicacao, fk_Vinculo_microchip_ID_Vinculo, Tipo_Operacao,
 Protocolo_Externo, Chave_Idempotencia, Situacao_Integracao, Quantidade_Tentativas, Data_Hora_Resposta, Mensagem_Erro) VALUES
(1, 1, NULL, 1, 'CADASTRO_ANIMAL',    'GOV-2026-000001', 'cadastro-animal-1-chip-1',  'CONFIRMADA', 1, '2026-02-01 10:05:00', NULL),
(2, 1, 1,    1, 'VACINA_ANTIRRABICA', NULL,              'vacina-antirrabica-1',        'PENDENTE',   1, NULL, 'Serviço governamental indisponível (timeout)');