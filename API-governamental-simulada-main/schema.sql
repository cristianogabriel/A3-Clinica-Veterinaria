CREATE DATABASE IF NOT EXISTS governamental_db;
USE governamental_db;

CREATE TABLE IF NOT EXISTS animal_gov (
  id_animal BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  codigo_microchip VARCHAR(30) NOT NULL UNIQUE,
  nome VARCHAR(100), data_nascimento DATE, sexo VARCHAR(20), cor VARCHAR(60),
  especie VARCHAR(60), raca VARCHAR(80), situacao VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
  data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, data_atualizacao DATETIME
);
CREATE TABLE IF NOT EXISTS operacao_gov (
  id_operacao BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  chave_idempotencia VARCHAR(100) NOT NULL UNIQUE, tipo_operacao VARCHAR(30) NOT NULL,
  protocolo VARCHAR(50) NOT NULL UNIQUE, situacao VARCHAR(20) NOT NULL,
  data_recebimento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, data_processamento DATETIME,
  resposta JSON, requisicao JSON NOT NULL
);
CREATE TABLE IF NOT EXISTS vacinacao_gov (
  id_vacinacao BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  id_animal BIGINT NOT NULL, id_operacao BIGINT NOT NULL UNIQUE,
  nome_vacina VARCHAR(100) NOT NULL, fabricante VARCHAR(100), numero_lote VARCHAR(60) NOT NULL,
  data_aplicacao DATE NOT NULL, dose VARCHAR(50), via_aplicacao VARCHAR(50), validade_lote DATE,
  observacoes TEXT, CRMV VARCHAR(10) NOT NULL, UF_CRMV CHAR(2) NOT NULL,
  nome_vet_responsavel VARCHAR(150) NOT NULL,
  CONSTRAINT fk_vacinacao_animal FOREIGN KEY (id_animal) REFERENCES animal_gov(id_animal),
  CONSTRAINT fk_vacinacao_operacao FOREIGN KEY (id_operacao) REFERENCES operacao_gov(id_operacao)
);
