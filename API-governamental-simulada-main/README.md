# API governamental simulada

API REST acadêmica independente para cadastro de animais por microchip, recebimento de vacinações antirrábicas e consulta de protocolos. Não representa integração oficial com órgão governamental.

## Requisitos e execução

- Java 21 e MySQL.
- Crie o banco `governamental_db` e um usuário com permissão de escrita. O arquivo `schema.sql` contém o esquema (inclui as três tabelas e as restrições de unicidade/FKs).
- Configure as variáveis `GOVERNO_DB_URL`, `GOVERNO_DB_USERNAME` e `GOVERNO_DB_PASSWORD` se os valores locais não forem `jdbc:mysql://localhost:3306/governamental_db`, `root` e senha vazia.
- Execute `./mvnw spring-boot:run`. O Hibernate atualiza tabelas existentes (`ddl-auto=update`).
- Para simular indisponibilidade nas escritas, configure `GOVERNO_SIMULAR_INDISPONIBILIDADE=true` e reinicie a aplicação. O padrão é `false`.

## Endpoints

Todas as escritas exigem `Idempotency-Key` (máximo 100 caracteres). Repetir a chave com o mesmo conteúdo retorna a resposta/protocolo originais; conteúdo diferente retorna 409.

```http
POST /api/governo/animais
Idempotency-Key: cadastro-001
Content-Type: application/json

{"codigo_microchip":"982000000000001","nome":"Rex","data_nascimento":"2022-04-10","sexo":"Macho","cor":"Caramelo","especie":"Cão","raca":"SRD"}
```

```http
GET /api/governo/animais/982000000000001
PUT /api/governo/animais/982000000000001
Idempotency-Key: atualizacao-001
Content-Type: application/json

{"codigo_microchip":"982000000000001","nome":"Rex Silva","data_nascimento":"2022-04-10","sexo":"Macho","cor":"Caramelo","especie":"Cão","raca":"SRD"}
```

```http
POST /api/governo/vacinacoes-antirrabicas
Idempotency-Key: vacina-001
Content-Type: application/json

{"codigo_microchip":"982000000000002","nome_animal":"Luna","especie":"Cão","cor":"Preta","sexo":"Fêmea","raca":"SRD","data_nascimento":"2021-08-02","nome_vacina":"Antirrábica","fabricante":"Fabricante Exemplo","numero_lote":"LOTE-123","data_aplicacao":"2026-11-14","dose":"1 dose","via_aplicacao":"Subcutânea","validade_lote":"2027-05-01","observacoes":"Dose anual","crmv":"12345","uf_crmv":"SP","nome_vet_responsavel":"Ana Veterinária"}
```

Vacinação cria um registro básico do animal se o microchip ainda não estiver cadastrado. Consulta de operação:

```http
GET /api/governo/operacoes/{protocolo}
```

Respostas de erro são JSON com `status`, `erro` e, para validações, `detalhes`. Códigos usados: 400, 404, 409, 500 e 503.
# API-governamental-simulada
