# Architect Agent

Este módulo contém o `Mimir` (anteriormente `ArchitectAgent`), responsável por tarefas relacionadas à arquitetura: análise arquitetural, propostas de mudanças estruturais e documentação arquitetural.

Estrutura recomendada:

- brain/lessons/ - lições específicas para o agente (arquitetura, patterns, guidelines)
- src/main/java - código do agente (implementa/estende `agent-core`)

Como usar (demo):

1. Build do projeto (na raiz):
   mvnw clean package

2. Executar Odin Orchestrator demo (usa Mimir):
   java -cp odin-orchestrator/target/odin-orchestrator-1.0.0-SNAPSHOT.jar br.com.valhalla.orchestrator.OdinApp

Notas:
- Mantenha as lições em UTF-8 sem BOM.
- O `Mimir` deve usar `agent-core` para acessar `LessonRepository` e `LLMProvider`.

See PERSONA.md for the LLM persona definition.
