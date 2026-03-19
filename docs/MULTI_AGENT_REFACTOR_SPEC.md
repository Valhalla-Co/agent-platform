# Multi-Agent Refactor Spec

Data: 2026-03-18
Autor: Plano automatizado

Resumo
------
Esta spec descreve o plano de refatoração para a arquitetura multi-agente do projeto "agent-platform".
O objetivo é consolidar o Odin como orquestrador (odin-orchestrator) e prover dois agentes especializados:
- dev-agent (nome mitológico: Wayland)
- architect-agent (nome mitológico: Mimir)

Requisitos chave
----------------
- Manter todo o processo de aprendizado pré-existente e os padrões já criados.
- Permitir que o repositório receba clones em `branches/` para que agentes possam atuar entre si em diferentes cópias do repo.
- Orquestração centralizada: todo tráfego (prompts, revisão de PRs, coordenação) passa pelo Odin.
- Cada agente tem sua própria "memory"/treinamento local/brain e um contrato mínimo com o Odin.

Inventário de módulos
---------------------
- odin-orchestrator/ (ou `odin-agent`/`odin-orchestrator` conforme layout)
- dev-agent/ (Wayland)
- architect-agent/ (Mimir)
- agent-core/ (biblioteca compartilhada)
- llm-providers/ (ex.: ollama-provider)
- branches/ (pasta opcional onde clones externos serão colocados para que agentes atuem)

Design de alto nível
--------------------
1. Odin (orquestrador)
   - Recebe solicitações externas (CLI, HTTP ou fila).
   - Autentica / valida contexto.
   - Roteia tarefas aos agentes especializados (dev / architect).
   - Mantém um registro de contexto global (session store) e coordena commits/PRs.

2. Dev Agent — Wayland
   - Foco: mudanças de implementação, geração de código, execução de builds locais, aplicação de correções simples.
   - Aprendizado: histórico de commits, padrões de codificação do projeto, testes unitários e estruturas de build (Maven).
   - Permissões: pode criar branches em `branches/`, abrir PRs automatizados e executar builds contidos.

3. Architect Agent — Mimir
   - Foco: propostas arquiteturais, refactorings de alto nível, documentação e geração de specs.
   - Aprendizado: diagramas, documentação do projeto, padrões de arquitetura já definidos (docs/).
   - Permissões: criar RFCs, editar specs em `docs/`, propor mudanças estruturais.

Contratos entre agentes
-----------------------
- Mensagens: JSON com campos { requestId, source, intent, payload, repoPath, branch }
- Resposta mínima: { requestId, status, artifacts:[paths], logs, suggestions }
- Artefatos: branches/PRs e arquivos em docs/ ou patch files

Branches como ambiente de trabalho
----------------------------------
- Estrutura: `branches/<agent>/<timestamp>-<task>`
- Cada agente trabalha em sua cópia; Odin valida e integra via PR.
- Permite simular concorrência e rollback fácil.

Fluxo de trabalho exemplo
-------------------------
1. Usuário pede ao Odin: "Refatore a camada X para usar Y".
2. Odin cria ticket/requestId e decide dividir a tarefa: Mimir cria plano e Wayland aplica mudanças.
3. Mimir escreve `docs/plan-<id>.md` e aprova plano.
4. Wayland cria branch `branches/wayland/20260318-01-refactor-layer-x`, aplica mudanças e executa `mvn -DskipTests clean package` em agent-core (ou módulo alvo).
5. Wayland push e abre PR; Odin executa checks e, se OK, mescla.

Compatibilidade com aprendizado anterior
---------------------------------------
- Preservar: logs de aprendizado, padrões, personas (arquivos em `dev-agent/brain`, `architect-agent/brain`).
- Extender: adicionar meta-dados que permitam versionar as "memories" por agente/versão do projeto.

Requisitos operacionais
-----------------------
- Ollama como provedor LLM local (documentado em `OLLAMA_STATUS.md`).
- Ambiente de build: JDK 21 (JAVA_HOME apontando para C:\DevTools\JDK\jdk-21.0.2).
- Runner isolado para builds nativos (Wayland pode necessitar de ambiente com Wayland/GUI; considerar containerização).

Plano de migração (passos executáveis)
--------------------------------------
1. Validar ambiente: Ollama, Java, mvnw (checks automatizados: `test-ollama.ps1`, `java -version`, `.
   mvnw.cmd -v`).
2. Estabilizar `agent-core` (rodar `scripts/clean_bom_and_build.ps1` e gerar artifacts).
3. Criar contratos JSON para mensagens entre Odin e agentes (arquivo: `odin-specs/agent-contract.json`).
4. Implementar adaptadores mínimos em cada agente para obedecer ao contrato (ex.: HTTP endpoints locais ou CLI wrappers).
5. Definir práticas de branches (skeletons) e criar utilitário para criação automática de branches em `branches/`.
6. Criar CI job (GitHub Actions) para validar builds e executar testes básicos em cada PR gerado por agentes.
7. Documentar o processo em `docs/MULTI_AGENT_REFACTOR_SPEC.md` e criar checklist para PRs automatizados.

Critérios de sucesso
--------------------
- `agent-core` compila e gera JARs locais automaticamente.
- Odin consegue acionar Wayland e Mimir via contrato e receber respostas estruturadas.
- Agents conseguem operar em `branches/` sem afetar `main` até que PR seja aprovado.

Riscos e bloqueadores
---------------------
- Dependências nativas (Wayland, JNI) que não compiam em ambientes CI sem setup específico.
- Permissões para setar JAVA_HOME Machine em hosts restritos (requer admin).
- Modelos LLM grandes exigem espaço local (codellama:7b ~3.8GB).

Tarefas imediatas (próximos tickets)
------------------------------------
- [ ] Criar `odin-specs/agent-contract.json`
- [ ] Implementar wrapper CLI light em `dev-agent` para operações Git + build
- [ ] Criar utilitário `scripts/create_branch_for_agent.ps1`
- [ ] Documentar políticas de merge/PR para agentes
- [ ] Criar CI job para builds (GitHub Actions)

Apêndice: nomes mitológicos
---------------------------
- dev-agent -> Wayland: referência à figura Ossetian/Wayland? (ainda que 'Wayland' seja associável a display server — aqui o nome lembra "way"/caminho e é curto, facilmente relacionado a desenvolvimento e integração com Wayland bindings em projetos nativos). O usuário já aprovou "Wayland".
- architect-agent -> Mimir: na mitologia nórdica, Mimir é guardião da sabedoria — apropriado para um agente que propõe arquitetura e mantém conhecimento.

Observações finais
------------------
Este documento é um ponto de partida. Posso criar os artefatos técnicos (agent-contract.json, scripts, wrappers) e executar os builds/checagens conforme o plano. Solicite que eu continue com a execução automatizada dos próximos passos.
