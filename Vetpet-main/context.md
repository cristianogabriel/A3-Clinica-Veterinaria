# Contexto do Projeto — Sistema de Clínica Veterinária

## 1. Visão geral

Este projeto consiste no desenvolvimento de um sistema acadêmico para gerenciamento das atividades básicas de uma clínica veterinária. A aplicação deverá centralizar informações de tutores, animais, veterinários, consultas e vacinações, permitindo que os profissionais registrem e consultem os dados necessários ao atendimento dos animais.

Além das funcionalidades internas da clínica, o projeto contempla uma integração com uma API governamental simulada, responsável por representar um serviço externo de registro de animais identificados por microchip RFID e recebimento de informações de vacinação antirrábica.

O sistema será desenvolvido no contexto de um trabalho universitário (A3 – SDM). Portanto, sua finalidade é demonstrar a aplicação dos conceitos de modelagem de dados, desenvolvimento de APIs, persistência relacional, validação de regras de negócio e comunicação entre serviços. Não se trata de um produto comercial nem de uma integração oficial com órgãos governamentais.

## 2. Objetivo

Desenvolver uma aplicação que permita à clínica veterinária organizar seus cadastros e registros de atendimento, mantendo o histórico dos animais e possibilitando a comunicação simulada de determinadas informações com um serviço externo.

Os objetivos principais são:

- Gerenciar o cadastro de veterinários e controlar o acesso à aplicação.
- Cadastrar e manter os dados dos tutores e seus endereços.
- Cadastrar animais, relacionando-os aos respectivos tutores, espécies e raças.
- Registrar e manter o histórico de microchips RFID vinculados aos animais.
- Registrar consultas veterinárias e informações clínicas.
- Registrar vacinações, seus dados de aplicação e próximas doses.
- Comunicar aplicações de vacinas antirrábicas à API governamental simulada.
- Registrar o resultado das comunicações externas e permitir novas tentativas quando necessário.

## 3. Escopo funcional

### 3.1. Autenticação e veterinários

O acesso às funcionalidades privadas será realizado por veterinários cadastrados. O sistema deverá considerar a situação do profissional e a confirmação da senha antes de permitir o login.

As senhas não poderão ser armazenadas em texto puro. O sistema deverá armazenar um hash apropriado e registrar informações relacionadas à confirmação da senha e ao último acesso. Para fins acadêmicos, o envio do código ou link de confirmação poderá ser simulado.

O sistema também deverá permitir o encerramento da sessão e proteger os endpoints privados contra acessos não autenticados ou não autorizados.

### 3.2. Tutores e endereços

O sistema deverá permitir cadastrar, consultar, atualizar e inativar tutores. Os dados contemplam nome completo, CPF, data de nascimento, e-mail, telefone, endereço, data de cadastro e situação do cadastro.

O CPF deverá ser validado e não poderá estar associado a mais de um tutor. Os dados de endereço incluem CEP, logradouro, número, complemento, bairro, cidade, UF e código IBGE do município, quando disponível.

A consulta de CEP deverá utilizar a API pública ViaCEP. Quando a consulta retornar um endereço válido, os campos poderão ser preenchidos automaticamente. O usuário também poderá complementar ou corrigir os dados manualmente. Uma falha ou indisponibilidade da ViaCEP não deverá impedir o preenchimento manual.

### 3.3. Espécies, raças e animais

O sistema deverá manter catálogos de espécies e raças. Cada raça deverá pertencer a uma espécie, e o cadastro de um animal deverá respeitar essa compatibilidade.

O cadastro de animais deverá contemplar identificador interno, nome, data de nascimento ou idade estimada, sexo, cor, peso atual, situação, espécie, raça quando aplicável e tutor responsável.

Todo animal deverá estar associado a um tutor. A raça poderá ficar vazia quando o animal não possuir raça definida (SRD). O sistema deverá permitir consultar e atualizar os dados cadastrais dos animais.

### 3.4. Microchips RFID

O sistema deverá permitir vincular um identificador de microchip RFID a um animal. Cada identificador deverá ser único, e o vínculo deverá registrar a data da operação e o veterinário responsável.

A leitura física do microchip não faz parte de uma exigência obrigatória do projeto. Para a demonstração acadêmica, o código poderá ser informado manualmente ou sua identificação poderá ser simulada.

Quando um microchip for substituído, o vínculo anterior deverá ser preservado no histórico. O sistema deverá manter a distinção entre vínculos anteriores e o vínculo atualmente ativo.

### 3.5. Consultas veterinárias

O sistema deverá permitir registrar consultas e atendimentos, sempre relacionando cada consulta a um animal e ao veterinário responsável.

Entre as informações registradas estão data e hora, motivo do atendimento, anamnese, sintomas, diagnóstico, tratamento prescrito, prescrição de medicamentos, peso, temperatura e observações.

As consultas deverão possuir uma situação que indique se estão agendadas, realizadas, canceladas ou se o animal não compareceu. Também deverá ser possível consultar o histórico de atendimentos de cada animal.

### 3.6. Vacinas

O sistema deverá permitir registrar vacinações associadas a um animal e ao veterinário responsável. O registro deverá contemplar nome e tipo da vacina, fabricante, número do lote, data de aplicação, dose ou etapa do esquema, via de aplicação, validade do lote, próxima dose quando aplicável, observações e situação.

As situações previstas são agendada, aplicada e cancelada. A classificação da vacina deverá identificar de forma estruturada se ela é antirrábica; essa identificação não deverá depender somente do texto informado no nome da vacina.

O sistema deverá manter o histórico de vacinações e permitir consultar as próximas doses previstas.

## 4. Integração com a API governamental simulada

### 4.1. Finalidade

O projeto deverá possuir uma segunda API, independente da API privada da clínica, que simulará um serviço governamental de registro de animais identificados por microchip.

Essa API simulada deverá permitir:

- Cadastrar externamente um animal.
- Consultar um animal pelo identificador do microchip RFID.
- Atualizar os dados de um animal registrado.
- Receber e registrar informações de vacinação antirrábica.
- Validar os dados recebidos e impedir registros duplicados.
- Gerar um protocolo para identificar cada operação processada.
- Consultar o resultado de uma operação por seu protocolo.
- Simular erros de validação e indisponibilidade do serviço.

A API governamental simulada deverá ter persistência própria ou uma separação lógica de dados claramente demonstrável. A comunicação entre as duas APIs deverá ocorrer por HTTP. O projeto não deverá afirmar que existe integração oficial com um sistema governamental real.

### 4.2. Regra central de vacinação antirrábica

Quando uma aplicação de vacina antirrábica for efetivamente registrada na clínica, o sistema deverá gerar uma solicitação de comunicação à API governamental simulada.

A comunicação deverá ocorrer depois do registro local da vacinação. A solicitação deverá conter, no mínimo:

- Identificador do microchip RFID.
- Identificação do animal.
- Data da aplicação.
- Identificação da vacina.
- Fabricante.
- Número do lote.
- Identificação do veterinário responsável.

A comunicação deverá ser solicitada mesmo que o animal ainda não esteja cadastrado no serviço externo.

Vacinas que não sejam antirrábicas não possuem obrigação de gerar essa comunicação.

### 4.3. Falhas, tentativas e idempotência

O registro local da vacinação deverá ser preservado mesmo quando a API governamental estiver indisponível ou retornar uma falha. Nesse cenário, a comunicação deverá permanecer registrada como pendente para que possa ser processada novamente.

O sistema deverá permitir novas tentativas automáticas ou manuais. A repetição de uma solicitação não poderá gerar registros duplicados no serviço externo. Para isso, o controle da integração deverá utilizar uma chave de idempotência.

Cada operação deverá manter informações que permitam acompanhar seu processamento, incluindo tipo de operação, animal relacionado, aplicação de vacina quando aplicável, protocolo externo, chave de idempotência, situação, quantidade de tentativas, datas de solicitação e resposta e eventual mensagem de erro.

Os estados previstos para uma integração são:

- **PENDENTE:** a comunicação ainda não foi concluída.
- **ENVIADA:** a solicitação foi enviada ao serviço externo.
- **CONFIRMADA:** o serviço externo confirmou o recebimento ou processamento.
- **FALHA:** ocorreu uma falha na operação.

É importante distinguir o registro local da vacinação da confirmação externa. Uma vacinação pode estar registrada na clínica sem que a API governamental tenha confirmado o recebimento.

## 5. Entidades e modelo de dados

Os diagramas conceitual e lógico e o script físico MySQL enviados representam a estrutura de dados inicial do projeto.

As principais entidades identificadas são:

| Entidade | Responsabilidade |
|---|---|
| Pessoas | Armazenar os dados pessoais compartilhados por tutores e veterinários. |
| Veterinários | Armazenar os dados profissionais, credenciais, situação e informações de acesso. |
| Tutores | Representar os responsáveis pelos animais e relacionar a pessoa ao endereço. |
| Endereços | Armazenar os dados de localização dos tutores. |
| Espécies | Manter o catálogo de espécies de animais. |
| Raças | Manter as raças e sua associação obrigatória com uma espécie. |
| Animais | Armazenar os dados dos animais e seus relacionamentos com tutor, espécie e raça. |
| Vinculo_microchip | Registrar os vínculos de microchips RFID, incluindo histórico, animal e veterinário responsável. |
| Consultas | Armazenar os atendimentos e informações clínicas dos animais. |
| Vacinas | Registrar as aplicações, dados da vacina, responsável e situação. |
| IntegracaoGoverno | Controlar as operações enviadas à API governamental simulada e seus resultados. |

A tabela `Pessoas` concentra dados comuns de identificação. `Veterinarios` e `Tutores` complementam esses dados com informações específicas de cada papel.

A tabela `Vinculo_microchip` foi estruturada para preservar o histórico de substituições. A tabela `IntegracaoGoverno` registra as operações externas e permite relacioná-las ao animal, à aplicação de vacina e ao vínculo de microchip correspondente, conforme o tipo de operação.

O script SQL também contém dados de exemplo para apoiar a configuração e os testes iniciais do banco de dados.

## 6. Estrutura técnica prevista

A solução contempla dois serviços:

**API privada da clínica:** responsável pelas funcionalidades internas, regras de negócio, autenticação, validações e persistência dos dados da clínica.

**API governamental simulada:** responsável por representar o serviço externo, receber operações da API privada, validar os dados, manter seus próprios registros e retornar protocolos e resultados.

A comunicação entre os serviços deverá ocorrer por HTTP. A API privada não deverá depender do acesso direto ao banco de dados da API governamental.

O documento de requisitos sugere Java para o backend, MySQL para o banco de dados, Postman para testes, Swagger/OpenAPI para documentação e Git para versionamento. Essas tecnologias são sugestões do documento, não imposições: outras escolhas poderão ser feitas desde que atendam aos requisitos e sejam justificadas.

A implementação deverá manter uma organização em componentes e camadas, separando responsabilidades de acesso a dados, regras de negócio e exposição dos endpoints.

## 7. Requisitos não funcionais

Além das funcionalidades, o projeto deverá observar os seguintes pontos:

- Comunicação entre frontend e backend por API REST.
- Persistência relacional para os dados da clínica.
- Validação dos dados no backend.
- Tratamento de erros HTTP, timeouts e indisponibilidade de serviços externos.
- Autenticação e autorização nos endpoints protegidos.
- Armazenamento seguro de credenciais.
- Rastreabilidade das operações de integração.
- Documentação dos endpoints.
- Organização do código em componentes e camadas.
- Comunicação demonstrável entre a API privada e a API governamental simulada.
- Separação dos serviços e dos dados.
- Identificação explícita da integração governamental como uma simulação acadêmica.

## 8. Limites do projeto

O desenvolvimento deverá permanecer compatível com o escopo acadêmico da atividade. Não é necessário transformar a aplicação em um produto comercial ou implementar infraestrutura de produção sem exigência dos requisitos.

Algumas funcionalidades possuem simplificações expressamente previstas:

- A confirmação de senha por e-mail poderá ser simulada.
- A leitura do microchip RFID poderá ser substituída pela entrada manual do código.
- A API governamental será uma simulação independente, não uma integração oficial.
- A indisponibilidade do serviço externo poderá ser simulada para demonstrar o tratamento de falhas.
- As tecnologias sugeridas poderão ser substituídas por outras escolhas justificadas.

Não devem ser adicionados módulos ou regras que não contribuam para os requisitos da atividade sem uma necessidade concreta.

## 9. Fluxos principais para demonstração

Os seguintes fluxos representam os comportamentos centrais que deverão ser demonstráveis:

### Fluxo A — Cadastro de tutor e animal

1. Cadastrar os dados pessoais do tutor.
2. Validar o CPF e impedir duplicidade.
3. Consultar o CEP pela ViaCEP ou preencher o endereço manualmente.
4. Cadastrar o animal.
5. Associar o animal ao tutor, à espécie e, quando aplicável, à raça.

### Fluxo B — Vínculo de microchip

1. Um veterinário autorizado informa o código do microchip.
2. O sistema valida a unicidade do identificador.
3. O vínculo é associado ao animal e ao veterinário responsável.
4. A data da vinculação é registrada.
5. Em uma substituição, o vínculo anterior permanece no histórico.

### Fluxo C — Consulta veterinária

1. Selecionar o animal.
2. Registrar a consulta e o veterinário responsável.
3. Informar os dados clínicos disponíveis.
4. Atualizar a situação da consulta.
5. Consultar posteriormente o histórico clínico do animal.

### Fluxo D — Vacinação antirrábica com sucesso

1. Registrar a aplicação da vacina antirrábica na clínica.
2. Gerar a operação de integração após a persistência local.
3. Enviar os dados obrigatórios à API governamental simulada.
4. Receber e armazenar o protocolo e o resultado.
5. Atualizar a situação da integração para refletir a resposta externa.

### Fluxo E — Vacinação antirrábica com falha externa

1. Registrar a aplicação da vacina na clínica.
2. Gerar a operação de integração.
3. Simular timeout ou indisponibilidade da API governamental.
4. Manter a vacinação registrada localmente.
5. Manter a integração pendente ou registrar a falha conforme o resultado do processamento.
6. Permitir uma nova tentativa.
7. Garantir que o reenvio não crie duplicidade no serviço externo.

### Fluxo F — Vacina não antirrábica

1. Registrar a aplicação de uma vacina não antirrábica.
2. Manter o registro no histórico local.
3. Não gerar a comunicação governamental obrigatória prevista para vacinas antirrábicas.

## 10. Orientações para o desenvolvimento

Este arquivo deve funcionar como contexto geral para quem for implementar, revisar ou apoiar o desenvolvimento do projeto.

Antes de criar uma funcionalidade, consulte os requisitos funcionais, as regras de negócio e o modelo de dados correspondente. Evite implementar comportamentos que contradigam esses documentos.

Priorize uma implementação simples, legível e suficiente para demonstrar os requisitos. Não introduza padrões arquiteturais, bibliotecas, serviços ou abstrações adicionais sem uma justificativa relacionada ao escopo.

Quando houver divergência entre este contexto resumido e o documento formal de requisitos, os requisitos formais e as decisões efetivamente adotadas pela equipe deverão ser considerados como referência para a implementação.

### Materiais de referência recebidos

- `Documenta de requisitos para o projeto a3.odt` — requisitos funcionais, requisitos não funcionais e regras de negócio.
- `Imagem Esquema Conceitual da Clinica Veterinaria final.png` — modelo conceitual.
- `Imagem Esquela logico da clinica.png` — modelo lógico.
- `clinica_veterinaria_Esquela fisico_mysql.sql` — esquema físico MySQL e dados de exemplo.

---

**Resumo:** desenvolver um sistema acadêmico de clínica veterinária com API privada, banco relacional e uma API governamental simulada. O sistema deverá gerenciar tutores, animais, veterinários, consultas, microchips e vacinas, com atenção especial ao fluxo de comunicação de vacinas antirrábicas, ao tratamento de falhas e à prevenção de duplicidade nas tentativas de integração.
