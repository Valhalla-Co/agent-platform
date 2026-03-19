# Spec: Adaptadores HTTP dos Agentes (Wayland / Mimir)

Objetivo
--------
Definir um contrato HTTP mínimo para que o Odin (orquestrador) invoque agentes (Wayland = dev-agent, Mimir = architect-agent) por rede, mantendo compatibilidade com o contrato JSON já descrito em `odin-specs/agent-contract.json` e com o CLI padronizado (`docs/AGENT_CLI_SPEC.md`). Implementação ficará para depois; este documento orienta como fazer.

Escopo
------
- Endpoints obrigatórios: /task (POST), /status/{requestId} (GET), /health (GET), /ready (GET).
- Payloads e respostas em JSON, baseados no contrato existente.
- Autenticação e segurança mínimas (headers e tokens).
- Fluxo assíncrono (202 + statusUrl) com opção de callback.
- Logging, métricas e observabilidade.
- Checklist de implementação futura.

Contrato de entrada (POST /task)
--------------------------------
- URL: `POST /task`
- Headers:
  - `Content-Type: application/json`
  - `X-Odin-Auth: <token>` (ou Bearer token) — validar no agente
- Body: JSON conforme `odin-specs/agent-contract.json` (mesmos campos do payload do CLI)
  - `requestId` (string, requerido)
  - `source` (string)
  - `intent` (string: build|analyze|refactor|review|test)
  - `payload` (object)
  - `repoPath` (string)
  - `branch` (string, opcional)
  - `timeoutSeconds` (int, opcional)

Resposta do POST /task (MVP)
----------------------------
- Status: 202 Accepted
- Body:
```
{
  "requestId": "req-20260318-0001",
  "statusUrl": "http://<host>:<port>/status/req-20260318-0001",
  "callbackAccepted": false
}
```

Contrato de status (GET /status/{requestId})
-------------------------------------------
- URL: `GET /status/{requestId}`
- Headers: `X-Odin-Auth` (opcional, conforme política)
- Resposta (200):
```
{
  "requestId": "req-20260318-0001",
  "status": "queued|running|done|failed",
  "exitCode": 0,
  "artifacts": ["path/to/patch.diff", "branches/..."],
  "logs": "path/to/logfile.log",
  "summary": "Resumo curto",
  "updatedAt": "2026-03-18T15:00:00Z"
}
```
- Se não encontrado: 404.

Health e readiness
------------------
- `GET /health` -> 200 e JSON `{ "status": "ok" }` (checa apenas processo do serviço HTTP).
- `GET /ready` -> 200 se o agente consegue aceitar tarefas (ex.: dependências locais, fs writável), senão 503.

Callback (opcional)
--------------------
- Campo opcional no payload: `callbackUrl` (string). Se presente, o agente, ao concluir, faz `POST callbackUrl` com o mesmo corpo de `/status` (status final).
- Fail-safe: mesmo com callback, Odin pode fazer polling no `/status`.

Autenticação e segurança
------------------------
- Mínimo: header `X-Odin-Auth` com um token compartilhado (env var `ODIN_AGENT_TOKEN`).
- Em produção: usar Bearer token + TLS (HTTPS) via proxy/ingress.
- Rate limiting: recomendado aplicar no proxy/ingress.
- Proibir operações destrutivas sem flag explícita no payload (ex.: push para remoto).

Observabilidade
---------------
- Logs: prefixar linhas com `requestId`.
- Métricas: expor `/metrics` (Prometheus) ou integrar no runtime escolhido (ex.: Micrometer no Spring Boot).
- Tracing: opcional; se usar, propagar `traceparent` header.

Interoperabilidade com o CLI
-----------------------------
- O serviço HTTP deve invocar a mesma lógica interna do CLI (reuso do executor de tarefas). O payload HTTP -> validação -> enfileira -> chama executor (CLI ou chamada interna) -> persiste resultado -> responde via statusUrl/callback.
- Recomendado: uma fila em memória simples para MVP; no futuro, usar worker/queue.

Stack sugerida (MVP)
--------------------
- Java + Spring Boot (leve, já compatível com Maven do projeto) ou Micronaut.
- Controller simples com endpoints acima; serviço que enfileira e executa tarefas chamando o executor local (CLI ou módulo Java).
- Config por env vars: PORT, ODIN_AGENT_TOKEN, WORK_DIR, CALLBACK_TIMEOUT.

Checklist de implementação futura
---------------------------------
- [ ] Criar módulo HTTP no `dev-agent` e `architect-agent` (ex.: `dev-agent-http` ou dentro do próprio módulo, com starter Spring Boot).
- [ ] Implementar endpoints: /task (POST), /status/{id} (GET), /health, /ready.
- [ ] Integrar com executor interno (reusar lógica do CLI, result.json, logs/).
- [ ] Implementar callback opcional (`callbackUrl`).
- [ ] Adicionar validação do schema do payload (`odin-specs/agent-contract.json`).
- [ ] Adicionar autenticação via header `X-Odin-Auth` ou Bearer.
- [ ] Adicionar métricas/health/readiness e logs com requestId.
- [ ] Escrever testes de integração (POST /task -> 202 -> GET /status -> done).
- [ ] Documentar variáveis de ambiente e exemplos de execução.
- [ ] (Opcional) Adicionar workflow de CI para rodar os testes HTTP.

Exemplos de uso (curl)
----------------------
```bash
# Enviar tarefa
curl -X POST http://localhost:8080/task \
  -H "Content-Type: application/json" \
  -H "X-Odin-Auth: $ODIN_AGENT_TOKEN" \
  -d @payload.json

# Consultar status
curl http://localhost:8080/status/req-20260318-0001 -H "X-Odin-Auth: $ODIN_AGENT_TOKEN"

# Health / Ready
curl http://localhost:8080/health
curl http://localhost:8080/ready
```

Notas finais
------------
- Priorize manter uma única lógica de execução (CLI/serviço interno) e apenas adaptar a interface HTTP para evitar duplicidade de código.
- Para o rollout, comece com o agente dev (Wayland), teste, e depois replique para o agente architect (Mimir).
