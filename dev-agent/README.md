# Dev Agent

Este módulo contém o `Wayland` (anteriormente `DevAgent`), responsável por tarefas relacionadas ao desenvolvimento: análise de código, sugestões de refatoração e automação de PRs.

Estrutura recomendada:

- brain/lessons/ - lições específicas para o agente (patterns, code smells, refactorings)
- src/main/java - código do agente (implementa/estende `agent-core`)

Como usar (demo):

1. Build do projeto (na raiz):
   mvnw clean package

2. Executar Odin Orchestrator demo (usa Wayland):
   java -cp odin-orchestrator/target/odin-orchestrator-1.0.0-SNAPSHOT.jar br.com.valhalla.orchestrator.OdinApp

Notas:
- Mantenha as lições em UTF-8 sem BOM.
- O `Wayland` deve usar `agent-core` para acessar `LessonRepository` e `LLMProvider`.

See PERSONA.md for the LLM persona definition.
