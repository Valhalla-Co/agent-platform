Este repositório segue uma arquitetura multi-modular. Foram adicionados módulos skeleton para acelerar o desenvolvimento:

- llm-providers/ollama-provider: implementação inicial do provider Ollama (HTTP)
- odin-orchestrator: orquestrador principal (runner OdinApp)
- dev-agent: agente focado em operações de código (skeleton)
- architect-agent: agente focado em arquitetura (skeleton)

Como usar (local):
1. Garantir Java (JAVA_HOME) e Maven estão instalados.
2. Opcional: instalar Ollama localmente e expor API (por padrão http://localhost:11434). Pode-se configurar OLLAMA_API_URL e OLLAMA_MODEL.
3. Build: mvnw.cmd -DskipTests=true clean install
4. Executar demo: java -cp odin-orchestrator/target/odin-orchestrator-1.0.0-SNAPSHOT.jar br.com.valhalla.orchestrator.OdinApp

Executando o demo local do OdinOrchestrator

1. Certifique-se de ter Java instalado e JAVA_HOME configurado (ex: JDK 21).
2. Build (na raiz do repositório):
   .\mvnw.cmd -DskipTests=true clean install

3. Executar o demo (após build):
   java -cp odin-orchestrator\target\odin-orchestrator-1.0.0-SNAPSHOT.jar br.com.valhalla.orchestrator.OdinApp

Notas:
- O `OllamaProvider` usa a variável de ambiente `OLLAMA_API_URL` (padrão http://localhost:11434) e `OLLAMA_MODEL` (padrão codellama:7b).
- Se a API do Ollama não estiver disponível, o provider `isAvailable()` retornará false e o provider retornará erro nas chamadas HTTP.

Próximos passos: implementar integração real entre OdinOrchestrator e agentes; melhorar LLMProvider com streaming e fallback CLI.
