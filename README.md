# A3 — Clínica Veterinária

Projeto em grupo da disciplina **Sistemas Distribuídos e Mobile**. Este repositório reúne a aplicação da clínica veterinária, a API governamental simulada e os materiais dos testes realizados por Cristiano Gabriel Firmino.

## Componentes

- [API governamental simulada](API-governamental-simulada-main/): serviço de cadastro, consulta e atualização de animais e registro de vacinação antirrábica.
- [Clínica veterinária](Vetpet-main/): aplicação da clínica desenvolvida pelo grupo.
- [Coleção de testes do Postman](A3_SDM_API_Governamental_Publicacao.postman_collection%20%282%29.json): requisições e verificações dos cenários da API governamental.
- [Relatório dos testes](Relatorio_Final_Testes_API_Governamental%20%281%29.pdf): resultados registrados, evidências e falhas observadas.

## Contribuição de Cristiano

Execução manual dos testes da API governamental utilizando o Postman e organização dos resultados e da documentação. Os cenários abrangem:

- Cadastro e consulta de animais e atualização cadastral.
- Registro de vacinação antirrábica.
- Validações de pedidos, incluindo microchip duplicado e vacinação sem CRMV.
- Repetição de pedidos com a mesma chave de idempotência.
- Indisponibilidade simulada do serviço e reenvio manual após a recuperação.

Em cada cenário foram comparados o código HTTP e os dados da resposta com o resultado esperado. Os resultados das execuções documentadas estão no relatório em PDF.

## Documentação no Postman

[Abrir a documentação dos testes da API governamental](https://documenter.getpostman.com/view/60319132/2sBYPv3CRR)

## Execução das aplicações

Consulte os arquivos `README.md` das pastas de cada aplicação para configuração e execução. Os testes devem ser realizados com os serviços e o banco de dados configurados. O cenário de indisponibilidade e recuperação exige mudança manual do modo do serviço.

## Equipe

André, Gabriel Lira, Micael Gomes, Jorge Luiz e Cristiano Gabriel Firmino. O projeto é um trabalho coletivo; a contribuição descrita acima corresponde aos testes realizados por Cristiano.
