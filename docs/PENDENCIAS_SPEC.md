Título: Spec — Pendências e próximos planos para integração LLM / Odin Orchestrator

Resumo

Este documento descreve as pendências (spec) e o roadmap imediato para completar a integração da LLM no projeto, estabilizar o orquestrador "Odin" e suportar múltiplos agentes (Wayland - dev, Mimir - architect) com aprendizado próprio. Contém objetivos, requisitos funcionais e não-funcionais, tarefas acionáveis (com caminhos de arquivo sugeridos), critérios de aceite, estimativas e riscos.

Objetivos

- Deixar o orquestrador (odin-orchestrator) executável localmente de maneira previsível, com fallback para um provider fake quando Ollama não estiver disponível.
- Preparar a integração com Ollama (ou outro provider real) com configuração clara (variáveis de ambiente) e um utilitário de verificação.
- Limpar warnings e polir logging/telemetria (usar SLF4J adequadamente e binding em teste/runtime conforme necessário).
- Criar testes de integração e um script de execução para facilitar demos locais.
- Documentar tarefas, estimativas e proprietários para que a equipe possa priorizar e executar.

Escopo

Incluído:
- Ajustes de código para estabilidade e limpeza (BaseAgent, agentes, provider).
- Fallback `FakeLLMProvider` (implementado) e validação.
- Testes unitários e um teste de integração leve que simula fluxo orquestrado (Odin -> Wayland/Mimir com provider fake).
- Scripts/README para rodar localmente (PowerShell .ps1 e uma entrada README em `odin-orchestrator`).
- Documentação das variáveis de ambiente (OLLAMA_API_URL, OLLAMA_MODEL).

Fora do escopo nessa spec:
- Produção de integração com provedores remotos além de Ollama (p.ex. OpenAI), a menos que solicitado.
- Deploy em ambientes remotos/CI (podemos adicionar depois como follow-up).

Requisitos funcionais (RF)

RF-1: O orquestrador deve detectar se `OllamaProvider.isAvailable()` e usar `FakeLLMProvider` se não estiver disponível.
RF-2: Deve existir um utilitário `OllamaCheck` que verifica disponibilidade e faz uma requisição de teste.
RF-3: Deve existir um script `run-odin.ps1` que: builda o reactor, monta classpath e executa `br.com.valhalla.orchestrator.OdinApp` com classes locais (sem instalar artefatos em .m2) para demos.
RF-4: Todos os debug prints `System.out.println` foram substituídos por `logger.debug`/`logger.info` e um binding SLF4J aparece em testes.
RF-5: Deve existir um teste de integração leve (JUnit) que cria `OdinOrchestrator` com `FakeLLMProvider` e valida que o resultado agregado tem sucesso e mensagens dos agentes.

Requisitos não-funcionais (RNF)

RNF-1: Tempo de execução do teste de integração < 3s em máquina de desenvolvimento.
RNF-2: Logs com nível configurable (INFO/DEBUG) via SLF4J.
RNF-3: Mudanças devem ser pequenas e localizadas (evitar grandes refactors nesta sprint).

Tarefas acionáveis (priorizadas, P0 = urgente para demo; P1 = curto prazo; P2 = backlog)

P0 - Demo / estabilidade (deve acontecer primeiro)
- T0.1: Adicionar `run-odin.ps1` no root que:
  - Executa `mvn -DskipTests package` no reactor;
  - Executa `mvn -pl :odin-orchestrator dependency:build-classpath -DincludeScope=runtime -Dmdep.outputFile=odin-cp.txt`;
  - Monta classpath com `target/classes` de cada módulo relevante e chama `java -cp` com `br.com.valhalla.orchestrator.OdinApp`.
  - Path sugerido: `run-odin.ps1` (root)
  - Estimativa: 1h
- T0.2: Garantir prints informativos no `OdinOrchestrator` (Provider em uso) — já implementado.
  - Arquivo: `odin-orchestrator/src/main/java/.../OdinOrchestrator.java` (feito)
  - Estimativa: 0.5h (verificação)
- T0.3: Garantir `FakeLLMProvider` existe e é fallback — já implementado em `agent-core`.
  - Arquivo: `agent-core/src/main/java/br/com/valhalla/core/llm/FakeLLMProvider.java` (feito)
  - Estimativa: 0.5h (verificação)

P1 - Qualidade/integração
- T1.1: Criar teste de integração leve `odin-orchestrator` que instancia `OdinOrchestrator` com `AgentContext.builder()` apontando para um diretório vazio e valida `AgentResult.success()` e que mensagens contenham `Wayland`/`Mimir`.
  - Local: `odin-orchestrator/src/test/java/.../OdinOrchestratorIT.java` (JUnit 5)
  - Estimativa: 2h
- T1.2: Ajustar e remover prints remanescentes (substituir por logger), configurar SLF4J binding para testes no parent POM (colocar `slf4j-simple` em scope test) — já parcialmente feito, revisar logs.
  - Arquivo: `pom.xml` (raiz) e remover System.out em agentes (feito parcialmente)
  - Estimativa: 1h
- T1.3: Documentar OLLAMA_SETUP.md com um parágrafo de como levantar Ollama localmente e variáveis de ambiente (ref.: `docs/OLLAMA_SETUP.md`) — revisar e melhorar se necessário.
  - Estimativa: 1h

P2 - Follow-ups (opcional, próximos sprints)
- T2.1: Criar CI job mínimo que executa `mvn -DskipTests package` e roda o teste de integração com FakeLLMProvider.
  - Estimativa: 3h
- T2.2: Integrar provider adicional (OpenAI) com configuração segura de chaves e exemplos de uso.
  - Estimativa: 1-2 dias
- T2.3: Expandir agentes (por exemplo, adicionar um "producer" ou "QA agent"), padronizar personas e treinamento contínuo.
  - Estimativa: variável

Critérios de aceite (CA)

- CA-1: Execução do `run-odin.ps1` (ou alternativa descrita) finaliza sem erros e imprime "Provider in use: ..." e uma linha com "OdinOrchestrator" result.
- CA-2: Teste de integração `OdinOrchestratorIT` passa com FakeLLMProvider.
- CA-3: Não haver System.out.println usados para debug em código principal; logs devem usar SLF4J.
- CA-4: Documentação mínima (README / docs) explicando como testar Ollama ou operar com Fake fallback.

Dependências

- Ollama (opcional) — se disponível, o `OllamaProvider` será usado automaticamente; caso contrário, fallback para `FakeLLMProvider`.
- Maven (mvnw disponível) e JDK 21 (requerido) — `JAVA_HOME` deve apontar para JDK 21 para compilar e rodar.

Riscos e mitigação

- Risco: Diferentes formas de execução do `exec:java` podem tentar resolver dependências no repositório local, causando erros "artifact absent".
  - Mitigação: usar a estratégia do `run-odin.ps1` que monta classpath a partir de `target/classes` ou executar `mvn install` antes de `exec:java`.
- Risco: Mudanças nos endpoints da API do Ollama (payloads/paths) podem quebrar `OllamaProvider`.
  - Mitigação: manter `OllamaProvider` com heurísticas de parsing e fallback para raw body; documentar versão suportada.

Entregáveis

- `docs/PENDENCIAS_SPEC.md` (este arquivo)
- `run-odin.ps1` (script de execução automatizada, a ser criado)
- Teste de integração `OdinOrchestratorIT` em `odin-orchestrator/src/test/java`
- Pequenas correções no código (remover prints, garantir SLF4J usage)
- (Opcional) CI job e `README` atualizado

Checklist curta (ação imediata)

- [ ] Criar `run-odin.ps1` no root (script que builda + monta classpath + java run)
- [ ] Criar `odin-orchestrator/src/test/java/.../OdinOrchestratorIT.java` (integração com FakeLLMProvider)
- [ ] Revisar e completar docs `docs/OLLAMA_SETUP.md` (passos e variáveis de ambiente)
- [ ] Ajustar POM/exec invocation se quisermos suporte direto a `mvn -pl :odin-orchestrator -am exec:java` sem instalar artefatos
- [ ] Opcional: adicionar job CI com build e integração leve

Estimativas totais (todas as ações P0+P1): ~1 dia de trabalho (6-8h) para ter demo estável e um teste de integração.

Observações finais

Se quiser, eu:
- Implemento agora `run-odin.ps1` (script PowerShell) e valido localmente (executo e trago saída); ou
- Crio e commito o teste de integração `OdinOrchestratorIT` e executo os testes; ou
- Faço o `mvn install` + exec:java run para você ver a saída com o fallback.

Diga qual ação você prefere que eu faça agora (por exemplo: "crie run-odin.ps1 e execute", "adicione teste de integração e rode", "faça mvn install e execute odin-orchestrator") e eu executo. 
