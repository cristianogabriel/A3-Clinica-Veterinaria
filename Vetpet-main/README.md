# Vetpet

## Integração com a API governamental simulada

Inicie primeiro o serviço `governo` (porta `8081`) e depois o IBMR. Ao registrar
ou concluir uma vacina antirrábica, o IBMR envia os dados ao endpoint
`POST /api/governo/vacinacoes-antirrabicas` após confirmar a gravação local.
O resultado e o protocolo ficam disponíveis em `GET /api/integracoes/animal/{animalId}`.

Se o envio falhar, a vacinação local é preservada e a integração fica em `FALHA`.
Depois de corrigir a causa (por exemplo, serviço indisponível ou microchip ainda
não vinculado), repita pelo endpoint autenticado
`POST /api/integracoes/{integracaoId}/tentativas`. A chave de idempotência da
vacina é mantida nos reenvios.

Configure `GOVERNO_API_URL` se a API simulada estiver em outro endereço
(padrão: `http://localhost:8081`).
