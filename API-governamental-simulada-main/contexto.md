Contexto do Projeto — API Governamental Simulada
1. Visão geral
Este repositório deve conter uma API REST independente que simula um serviço governamental de registro de animais identificados por microchip RFID e recebimento de vacinações antirrábicas.

A API será desenvolvida como parte de um projeto acadêmico (A3) de gerenciamento de clínica veterinária. Ela será consumida pela API privada da clínica por meio de requisições HTTP.

O serviço é fictício e acadêmico. Não existe integração oficial com órgãos ou sistemas governamentais reais.

Objetivo desta etapa: implementar somente a API governamental simulada. Não desenvolver a API da clínica, frontend ou qualquer outro sistema.

A implementação deve ser pequena, direta e funcional. O projeto não é comercial e não exige arquitetura ou infraestrutura de produção.
2. Responsabilidade da API
A API governamental simulada deve:
1. Cadastrar animais identificados por microchip.
2. Consultar animais pelo código do microchip.
3. Atualizar os dados cadastrais de animais.
4. Receber e registrar vacinações antirrábicas.
5. Validar os dados recebidos.
6. Impedir duplicidade de operações por chave de idempotência.
7. Gerar um protocolo único para cada operação de escrita.
8. Permitir consultar o resultado de uma operação pelo protocolo.
9. Permitir simular erros de validação e indisponibilidade.
Não implementar funcionalidades além dessas sem uma necessidade identificada nos requisitos.
3. Materiais de referência
Os seguintes arquivos foram fornecidos para orientar a implementação:
- Esquema logico do governamental.brM3: modelo lógico do banco governamental.
- Imagem_esquema fisico do governo 01.png: diagrama visual do modelo físico.
- Governamental_db_Esquema fisico: script SQL de criação do banco e das tabelas.
- Documenta de requisitos para o projeto a3.odt: requisitos funcionais, não funcionais e regras de negócio da atividade.
O script físico SQL é a referência para os nomes e tipos das colunas na implementação. O diagrama deve ser consultado para compreender as relações entre as entidades.

Existe uma divergência de tamanho do campo codigo_microchip: o diagrama apresenta VARCHAR(20), enquanto o script físico apresenta VARCHAR(30). Para a implementação, adotar VARCHAR(30), conforme o script físico, a menos que uma decisão documentada da equipe determine o contrário.

O script SQL fornecido contém duas vírgulas duplicadas na declaração dos campos CRMV e UF_CRMV da tabela vacinacao_gov. Corrigir esse erro de sintaxe sem alterar desnecessariamente o modelo.
4. Escopo técnico e simplicidade
A API deve ser uma aplicação independente, com execução própria e banco de dados próprio.

Tecnologias preferenciais:
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Jakarta Validation
- MySQL
- Maven ou Gradle, conforme a estrutura já existente no repositório
Organização simples em camadas:
- controller: endpoints REST.
- service: regras de negócio.
- repository: acesso ao banco.
- entity: entidades persistidas.
- dto: dados de entrada e saída.
- exception: tratamento de erros.
- config: configurações necessárias.
Não implementar autenticação, usuários, Spring Security, frontend, filas, mensageria, microsserviços adicionais ou Clean Architecture completa. Não adicionar dependências sem necessidade.

A API deve expor JSON e utilizar HTTP. Não deve acessar diretamente o banco de dados da clínica.
5. Modelo de dados
O banco de dados deverá se chamar governamental_db e possuir as três tabelas do modelo fornecido.
5.1. animal_gov
Armazena os animais conhecidos pelo serviço simulado.
Campo	Tipo	Observação
id_animal	BIGINT	Chave primária, auto incremento
codigo_microchip	VARCHAR(30)	Obrigatório e único
nome	VARCHAR(100)	Nome do animal
data_nascimento	DATE	Data de nascimento
sexo	VARCHAR(20)	Sexo
cor	VARCHAR(60)	Cor
especie	VARCHAR(60)	Espécie
raca	VARCHAR(80)	Raça
situacao	VARCHAR(20)	Obrigatório; padrão ATIVO
data_cadastro	DATETIME	Obrigatório; padrão de criação
data_atualizacao	DATETIME	Preenchido em atualizações


Regras:
- codigo_microchip não pode se repetir.
- A consulta de animal deve utilizar o código do microchip.
- O identificador do microchip não deve ser alterado pelo endpoint de atualização.
5.2. operacao_gov
Registra as operações de escrita recebidas pela API.
Campo	Tipo	Observação
id_operacao	BIGINT	Chave primária, auto incremento
chave_idempotencia	VARCHAR(100)	Obrigatória e única
tipo_operacao	VARCHAR(30)	Tipo da operação
protocolo	VARCHAR(50)	Obrigatório e único
situacao	VARCHAR(20)	Estado da operação
data_recebimento	DATETIME	Data de recebimento
data_processamento	DATETIME	Data de conclusão do processamento
resposta	JSON	Resultado armazenado da operação


Tipos previstos:
- CADASTRO_ANIMAL
- ATUALIZACAO_ANIMAL
- VACINACAO
A consulta GET de animal não precisa criar registro de operação nem protocolo. As operações de escrita devem gerar protocolo.
5.3. vacinacao_gov
Armazena as vacinações antirrábicas recebidas.
Campo	Tipo	Observação
id_vacinacao	BIGINT	Chave primária, auto incremento
id_animal	BIGINT	Obrigatório; FK para animal_gov
id_operacao	BIGINT	Obrigatório; FK para operacao_gov e único
nome_vacina	VARCHAR(100)	Obrigatório
fabricante	VARCHAR(100)	Fabricante
numero_lote	VARCHAR(60)	Obrigatório
data_aplicacao	DATE	Obrigatória
dose	VARCHAR(50)	Dose
via_aplicacao	VARCHAR(50)	Via de aplicação
validade_lote	DATE	Validade do lote
observacoes	TEXT	Observações
CRMV	VARCHAR(10)	Obrigatório
UF_CRMV	CHAR(2)	Obrigatório
nome_vet_responsavel	VARCHAR(150)	Obrigatório


Regras:
- Toda vacinação deve estar relacionada a um animal.
- Toda vacinação deve estar relacionada a uma operação.
- id_operacao deve ser único em vacinacao_gov, impedindo que uma operação gere duas vacinações.
- O animal deve ser localizado pelo microchip informado na requisição.
6. Endpoints obrigatórios
Implementar somente estes cinco endpoints:
Método	Rota	Responsabilidade
POST	/api/governo/animais	Cadastrar animal
GET	/api/governo/animais/{microchip}	Consultar animal pelo microchip
PUT	/api/governo/animais/{microchip}	Atualizar dados do animal
POST	/api/governo/vacinacoes-antirrabicas	Receber vacinação antirrábica
GET	/api/governo/operacoes/{protocolo}	Consultar operação pelo protocolo


6.1. Cadastrar animal
POST /api/governo/animais

Recebe os dados cadastrais do animal.

Comportamento:
1. Validar os campos recebidos.
2. Verificar se o microchip já está cadastrado.
3. Criar o animal.
4. Registrar uma operação do tipo CADASTRO_ANIMAL.
5. Gerar um protocolo único.
6. Armazenar o resultado da operação.
7. Retornar os dados relevantes e o protocolo.
O microchip é obrigatório e não pode estar duplicado.
6.2. Consultar animal
GET /api/governo/animais/{microchip}

Localiza e retorna o animal pelo código do microchip.

Se não existir, retornar HTTP 404.

Não é necessário gerar protocolo para essa consulta.
6.3. Atualizar animal
PUT /api/governo/animais/{microchip}

Atualiza os dados cadastrais do animal identificado pelo microchip da rota.

Comportamento:
1. Verificar se o animal existe.
2. Validar os dados recebidos.
3. Atualizar os campos permitidos.
4. Preservar o código do microchip.
5. Registrar uma operação do tipo ATUALIZACAO_ANIMAL.
6. Gerar protocolo e armazenar o resultado.
Se o animal não existir, retornar HTTP 404.
6.4. Receber vacinação antirrábica
POST /api/governo/vacinacoes-antirrabicas

Recebe os dados da vacinação realizada na clínica.

O corpo deverá conter, no mínimo:
- codigo_microchip
- nome_animal
- nome_vacina
- fabricante
- numero_lote
- data_aplicacao
- dose
- via_aplicacao
- validade_lote
- observacoes
- CRMV
- UF_CRMV
- nome_vet_responsavel
O corpo também poderá receber os dados cadastrais complementares do animal (data_nascimento, especie, cor, sexo e raca) para permitir o cadastro inicial quando necessário.

Regra importante: a clínica deve comunicar a vacinação antirrábica mesmo quando o animal ainda não estiver cadastrado no serviço governamental. Portanto, se o microchip ainda não existir em animal_gov, a API deverá criar um registro básico do animal utilizando os dados recebidos na requisição e, em seguida, registrar a vacinação. Se o animal já existir, deverá reutilizar o registro encontrado pelo microchip, sem criar outro animal.

Comportamento:
1. Validar a chave de idempotência.
2. Verificar se a chave já foi processada.
3. Se for uma nova solicitação, localizar o animal pelo microchip.
4. Se o animal não existir, cadastrá-lo com os dados recebidos.
5. Registrar a operação do tipo VACINACAO.
6. Registrar a vacinação associada ao animal e à operação.
7. Gerar protocolo.
8. Armazenar o resultado.
9. Retornar a confirmação do processamento.
A criação eventual do animal e o registro da vacinação devem ocorrer na mesma transação, para evitar registros incompletos.
6.5. Consultar operação
GET /api/governo/operacoes/{protocolo}

Localiza uma operação pelo protocolo e retorna:
- Protocolo.
- Tipo da operação.
- Situação.
- Data de recebimento.
- Data de processamento.
- Resultado armazenado.
Se o protocolo não existir, retornar HTTP 404.
7. Idempotência
A idempotência é obrigatória para as operações de escrita.

A clínica deverá enviar uma chave no header:

Idempotency-Key: <chave-unica-da-requisicao>

Aplicar a regra nos três endpoints de escrita:
- Cadastro de animal.
- Atualização de animal.
- Registro de vacinação.
Comportamento:
1. Receber a chave de idempotência.
2. Procurar a chave em operacao_gov.
3. Se não existir, executar a operação e armazenar seu resultado.
4. Se já existir, não executar novamente a operação.
5. Retornar o mesmo protocolo e o resultado anteriormente armazenado.
Uma repetição não poderá gerar novo animal, nova vacinação ou novo protocolo.

A unicidade da chave deve ser garantida também pelo banco de dados, não apenas por uma consulta na aplicação.

Se a mesma chave for reutilizada com uma solicitação diferente, retornar HTTP 409, sem executar uma nova operação. Para isso, a aplicação pode comparar o conteúdo da requisição com o conteúdo originalmente armazenado, usando uma solução simples.

O registro da operação, a alteração do animal e a vacinação correspondente devem respeitar a consistência transacional.
8. Protocolos e estados
Toda operação de escrita processada deve possuir protocolo único. Pode ser utilizado UUID.

A operação deve armazenar seu tipo, situação, datas e resultado.

Utilizar estados simples e coerentes com o processamento:
- CONFIRMADA: operação processada com sucesso.
- FALHA: operação processada, mas rejeitada por uma regra ou erro de negócio.
Não criar um fluxo de processamento assíncrono. O processamento é síncrono: a API recebe, valida, persiste e responde na mesma requisição.

A indisponibilidade simulada deve retornar HTTP 503 e não deve produzir uma operação confirmada.
9. Validação e respostas HTTP
Utilizar Jakarta Validation e validações de negócio no service.

Respostas esperadas:
Situação	HTTP
Cadastro realizado	201 Created
Atualização realizada	200 OK
Consulta realizada	200 OK
Dados inválidos	400 Bad Request ou 422 Unprocessable Entity
Registro não encontrado	404 Not Found
Microchip duplicado	409 Conflict
Chave reutilizada com conteúdo diferente	409 Conflict
Serviço indisponível	503 Service Unavailable
Erro inesperado	500 Internal Server Error


As respostas de erro devem ser claras e consistentes. Não retornar stack trace, SQL ou detalhes internos da aplicação.

Validar, conforme aplicável:
- Presença do microchip.
- Limite de tamanho dos campos.
- Datas em formato válido.
- Número do lote.
- Identificação do veterinário e CRMV.
- Campos obrigatórios da vacinação.
- Existência e unicidade dos registros.
Não inventar validações de domínio que não estejam justificadas pelo modelo ou pelos requisitos.
10. Simulação de indisponibilidade
A API deve permitir simular uma indisponibilidade sem precisar desligar a aplicação.

Utilizar uma propriedade simples, por exemplo:

governo.simular-indisponibilidade=false

Quando habilitada, os endpoints de escrita devem retornar HTTP 503.

A configuração deve ser simples de alterar no application.properties ou por variável de ambiente.

Não criar endpoint administrativo apenas para ativar a indisponibilidade. A simulação de dados inválidos já pode ser feita por requisições com payload inválido.
11. Banco de dados
Utilizar o banco governamental_db e as três tabelas do script fornecido:
- animal_gov
- operacao_gov
- vacinacao_gov
Respeitar as chaves primárias, estrangeiras e restrições de unicidade do modelo.

Corrigir somente erros necessários para que o SQL funcione. Não adicionar tabelas sem justificativa.

A tabela operacao_gov deve garantir:
- chave_idempotencia única.
- protocolo único.
A tabela animal_gov deve garantir:
- codigo_microchip único.
A tabela vacinacao_gov deve garantir:
- FK para animal.
- FK para operação.
- id_operacao único.
12. Testes mínimos
Criar testes ou uma coleção de requisições Postman que demonstre:
1. Cadastro de animal com microchip.
2. Tentativa de cadastro com microchip duplicado.
3. Consulta de animal existente.
4. Consulta de animal inexistente.
5. Atualização de animal.
6. Registro de vacinação para animal já cadastrado.
7. Registro de vacinação para animal ainda não cadastrado, criando seu registro básico.
8. Reenvio da mesma vacinação com a mesma chave de idempotência.
9. Confirmação de que o reenvio retorna o mesmo protocolo e não duplica a vacinação.
10. Reutilização de chave com payload diferente.
11. Consulta de operação pelo protocolo.
12. Envio de dados inválidos.
13. Simulação de indisponibilidade com HTTP 503.
13. Critérios de conclusão
A API estará pronta quando:
- Executar independentemente da API da clínica.
- Utilizar seu próprio banco MySQL.
- Implementar os cinco endpoints definidos.
- Cadastrar, consultar e atualizar animais por microchip.
- Receber vacinações antirrábicas.
- Cadastrar um animal básico quando receber vacinação de um microchip ainda desconhecido.
- Gerar e consultar protocolos.
- Garantir idempotência e evitar duplicidade.
- Validar dados e responder com códigos HTTP adequados.
- Permitir simular indisponibilidade.
- Possuir instruções claras de execução e exemplos de requisições.
14. Orientação para iniciar
Antes de codificar:
1. Examine a estrutura atual do repositório.
2. Leia este context.md.
3. Abra e analise o script Governamental_db_Esquema fisico.
4. Consulte o diagrama físico e o modelo lógico.
5. Leia os requisitos governamentais do documento da A3.
6. Identifique o que já existe e o que ainda precisa ser implementado.
Em seguida, implemente a aplicação diretamente no repositório. Não entregue apenas um planejamento.

Se o repositório estiver vazio, crie a estrutura mínima necessária. Se já houver código, preserve e aproveite o que estiver correto.

Ao finalizar, documente como configurar o banco, iniciar a aplicação e testar os endpoints.

Prioridade: uma API governamental simulada pequena, independente, funcional e fácil de demonstrar em uma apresentação acadêmica. Não faça overengineering.