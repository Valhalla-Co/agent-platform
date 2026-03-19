# 🎨 Diagrama de Arquitetura - Agent Platform

## 📊 ARQUITETURA ATUAL vs PROPOSTA

### **ANTES (Atual)**
```
┌─────────────────────────────────────────┐
│         agent-platform                  │
│                                         │
│  ┌───────────────────────────────────┐  │
│  │       odin-agent (módulo)         │  │
│  │                                   │  │
│  │  ┌─────────────┐  ┌────────────┐ │  │
│  │  │  OdinCli    │  │ OdinAgent  │ │  │
│  │  └─────────────┘  └────────────┘ │  │
│  │                                   │  │
│  │  ┌─────────────────────────────┐ │  │
│  │  │  LLM (OllamaProvider)       │ │  │
│  │  │  - Acoplado ao Odin         │ │  │
│  │  │  - Não reutilizável         │ │  │
│  │  └─────────────────────────────┘ │  │
│  │                                   │  │
│  │  ┌─────────────────────────────┐ │  │
│  │  │  OdinBrain                  │ │  │
│  │  │  - Específico do Odin       │ │  │
│  │  └─────────────────────────────┘ │  │
│  │                                   │  │
│  │  core/ discovery/ patterns/      │  │
│  │  specs/ git/ model/              │  │
│  └───────────────────────────────────┘  │
│                                         │
│  ❌ Monolítico                          │
│  ❌ Não extensível                      │
│  ❌ Acoplamento alto                    │
└─────────────────────────────────────────┘
```

### **DEPOIS (Proposta)**
```
┌─────────────────────────────────────────────────────────────────────┐
│                        agent-platform                               │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      agent-core (SHARED)                    │   │
│  │                                                             │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │   │
│  │  │   Agent      │  │  BaseAgent   │  │  AgentFactory   │  │   │
│  │  │ (interface)  │  │  (abstract)  │  │                 │  │   │
│  │  └──────────────┘  └──────────────┘  └─────────────────┘  │   │
│  │                                                             │   │
│  │  ┌────────────────────────────────────────────────────┐    │   │
│  │  │            LLM Abstraction Layer                   │    │   │
│  │  │  - LLMProvider (interface)                        │    │   │
│  │  │  - LLMResponse, LLMConfig                         │    │   │
│  │  │  - PromptTemplate                                 │    │   │
│  │  └────────────────────────────────────────────────────┘    │   │
│  │                                                             │   │
│  │  ┌────────────────────────────────────────────────────┐    │   │
│  │  │         Knowledge Base (Brain)                     │    │   │
│  │  │  - Lesson, LessonRepository                       │    │   │
│  │  └────────────────────────────────────────────────────┘    │   │
│  │                                                             │   │
│  │  ┌────────────────────────────────────────────────────┐    │   │
│  │  │              CLI Framework                         │    │   │
│  │  │  - AgentCli (base), CommandProcessor             │    │   │
│  │  └────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ⬇️ uses                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    llm-providers (PLUGGABLE)                │   │
│  │                                                             │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │   │
│  │  │   Ollama     │  │   OpenAI     │  │   Claude     │     │   │
│  │  │  Provider    │  │   Provider   │  │   Provider   │     │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘     │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ⬇️ implements                          │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐          │
│  │  odin-agent   │  │  thor-agent   │  │  loki-agent   │  ...     │
│  │               │  │               │  │               │          │
│  │ ┌───────────┐ │  │ ┌───────────┐ │  │ ┌───────────┐ │          │
│  │ │ OdinAgent │ │  │ │ ThorAgent │ │  │ │ LokiAgent │ │          │
│  │ │ extends   │ │  │ │ extends   │ │  │ │ extends   │ │          │
│  │ │ BaseAgent │ │  │ │ BaseAgent │ │  │ │ BaseAgent │ │          │
│  │ └───────────┘ │  │ └───────────┘ │  │ └───────────┘ │          │
│  │               │  │               │  │               │          │
│  │ ┌───────────┐ │  │ ┌───────────┐ │  │ ┌───────────┐ │          │
│  │ │  OdinCli  │ │  │ │  ThorCli  │ │  │ │  LokiCli  │ │          │
│  │ │  extends  │ │  │ │  extends  │ │  │ │  extends  │ │          │
│  │ │ AgentCli  │ │  │ │ AgentCli  │ │  │ │ AgentCli  │ │          │
│  │ └───────────┘ │  │ └───────────┘ │  │ └───────────┘ │          │
│  │               │  │               │  │               │          │
│  │ Análise de    │  │ Deploy &      │  │ Testing &     │          │
│  │ Código        │  │ Infra         │  │ Quality       │          │
│  └───────────────┘  └───────────────┘  └───────────────┘          │
│                                                                     │
│  ✅ Modular                                                         │
│  ✅ Extensível                                                      │
│  ✅ Baixo acoplamento                                               │
│  ✅ Alto reuso                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 FLUXO DE EXECUÇÃO

### **Criar Novo Agente (Freya - Documentation)**

```
1. CRIAR MÓDULO
   └─> freya-agent/
       ├── pom.xml (depende de agent-core)
       ├── FreyaAgent.java
       └── FreyaCli.java

2. IMPLEMENTAR AGENT
   ┌────────────────────────────────────────────┐
   │ public class FreyaAgent extends BaseAgent │
   │ {                                          │
   │   @Override                                │
   │   protected AgentResult doExecute(...) {   │
   │     // Usa llm do agent-core ✅           │
   │     LLMResponse docs = llm.chat(prompt);   │
   │                                            │
   │     // Usa brain do agent-core ✅         │
   │     List<Lesson> lessons = brain.find();  │
   │                                            │
   │     // Lógica específica                  │
   │     generateDocumentation(docs);           │
   │   }                                        │
   │ }                                          │
   └────────────────────────────────────────────┘

3. IMPLEMENTAR CLI
   ┌────────────────────────────────────────────┐
   │ public class FreyaCli extends AgentCli     │
   │ {                                          │
   │   @Override                                │
   │   protected Agent createAgent(LLM llm) {   │
   │     return new FreyaAgent(llm);            │
   │   }                                        │
   │ }                                          │
   └────────────────────────────────────────────┘

4. PRONTO! 🎉
   - Tempo: ~4-6 horas
   - Reuso: ~80% do código
   - LLM: ✅ Compartilhado
   - Brain: ✅ Compartilhado
```

---

## 🔌 DIAGRAMA DE DEPENDÊNCIAS

```
┌──────────────────────────────────────────────────────────┐
│                    Maven Modules                          │
└──────────────────────────────────────────────────────────┘

    agent-platform (parent pom)
           │
           ├──> agent-core
           │    │
           │    └──> (sem dependências externas)
           │         - Java 21
           │         - Jackson (JSON)
           │         - SLF4J (logging)
           │
           ├──> llm-providers
           │    │
           │    ├──> ollama-provider
           │    │    └──> depends on: agent-core, okhttp
           │    │
           │    ├──> openai-provider
           │    │    └──> depends on: agent-core, openai-java
           │    │
           │    └──> claude-provider
           │         └──> depends on: agent-core, anthropic-sdk
           │
           ├──> odin-agent
           │    └──> depends on: agent-core, llm-providers
           │
           ├──> thor-agent
           │    └──> depends on: agent-core, llm-providers
           │
           ├──> loki-agent
           │    └──> depends on: agent-core, llm-providers
           │
           └──> agent-launcher
                └──> depends on: agent-core, all agents

┌──────────────────────────────────────────────────────────┐
│               Dependency Flow                             │
└──────────────────────────────────────────────────────────┘

All Agents ────► agent-core ◄──── llm-providers
                     │
                     └────► Java 21 + Basic Libs
```

---

## 🎯 EXEMPLO PRÁTICO: Criar ThorAgent

### **1. Criar Módulo**
```xml
<!-- thor-agent/pom.xml -->
<project>
    <parent>
        <groupId>br.com.valhalla</groupId>
        <artifactId>agent-platform</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>thor-agent</artifactId>
    
    <dependencies>
        <dependency>
            <groupId>br.com.valhalla</groupId>
            <artifactId>agent-core</artifactId>
        </dependency>
        <dependency>
            <groupId>br.com.valhalla</groupId>
            <artifactId>ollama-provider</artifactId>
        </dependency>
    </dependencies>
</project>
```

### **2. Implementar Agent**
```java
package br.com.valhalla.thor;

import br.com.valhalla.core.Agent;
import br.com.valhalla.core.BaseAgent;
import br.com.valhalla.core.AgentContext;
import br.com.valhalla.core.AgentResult;

public class ThorAgent extends BaseAgent {
    
    @Override
    protected AgentResult doExecute(AgentContext context) {
        // 1. Analisar infraestrutura com LLM (herdado de BaseAgent)
        String prompt = buildInfrastructurePrompt(context);
        LLMResponse analysis = llm.chat(prompt);
        
        // 2. Aplicar conhecimento do brain (herdado de BaseAgent)
        List<Lesson> deployLessons = brain.findByTag("deployment");
        
        // 3. Executar deploy
        DeployResult result = executeDeployment(analysis, deployLessons);
        
        return new ThorResult(result);
    }
    
    private String buildInfrastructurePrompt(AgentContext context) {
        return PromptTemplate.infrastructureAnalysis(
            context.getProjectPath(),
            context.getInfrastructureFiles()
        );
    }
}
```

### **3. Implementar CLI**
```java
package br.com.valhalla.thor;

import br.com.valhalla.core.cli.AgentCli;
import br.com.valhalla.core.Agent;
import br.com.valhalla.core.llm.LLMProvider;

public class ThorCli extends AgentCli {
    
    @Override
    protected Agent createAgent(LLMProvider llm) {
        return new ThorAgent(llm);
    }
    
    @Override
    protected String getAgentName() {
        return "Thor";
    }
    
    @Override
    protected String getBanner() {
        return """
            ⚡ THOR AGENT ⚡
            Deploy & Infrastructure Automation
            """;
    }
    
    public static void main(String[] args) {
        new ThorCli().run(args);
    }
}
```

### **4. Build & Run**
```bash
# Build
mvn clean install

# Run
java -jar thor-agent/target/thor-agent-1.0.0-SNAPSHOT.jar

# Ou via launcher
java -jar agent-launcher.jar thor
```

**Tempo total:** ~6 horas  
**Reuso de código:** ~80%  
**LLM:** ✅ Reutilizado  
**Brain:** ✅ Reutilizado  

---

## 📦 ESTRUTURA DE DIRETÓRIOS FINAL

```
agent-platform/
│
├── pom.xml                          (parent)
│
├── agent-core/                      🆕 Shared abstractions
│   ├── pom.xml
│   └── src/main/java/br/com/valhalla/core/
│       ├── Agent.java
│       ├── BaseAgent.java
│       ├── AgentFactory.java
│       ├── llm/
│       │   ├── LLMProvider.java
│       │   ├── LLMResponse.java
│       │   ├── LLMConfig.java
│       │   └── PromptTemplate.java
│       ├── brain/
│       │   ├── KnowledgeBase.java
│       │   ├── Lesson.java
│       │   └── LessonRepository.java
│       └── cli/
│           ├── AgentCli.java
│           └── CommandProcessor.java
│
├── llm-providers/                   🆕 LLM implementations
│   ├── pom.xml
│   ├── ollama-provider/
│   │   ├── pom.xml
│   │   └── src/.../OllamaProvider.java
│   ├── openai-provider/
│   └── claude-provider/
│
├── odin-agent/                      ♻️  Refactored
│   ├── pom.xml
│   └── src/main/java/br/com/valhalla/odin/
│       ├── OdinAgent.java          (extends BaseAgent)
│       ├── OdinCli.java            (extends AgentCli)
│       ├── analyzers/
│       ├── patterns/
│       └── specs/
│
├── thor-agent/                      🆕 Deploy agent
│   ├── pom.xml
│   └── src/main/java/br/com/valhalla/thor/
│       ├── ThorAgent.java
│       └── ThorCli.java
│
├── loki-agent/                      🆕 Testing agent
│   ├── pom.xml
│   └── src/main/java/br/com/valhalla/loki/
│       ├── LokiAgent.java
│       └── LokiCli.java
│
├── agent-launcher/                  🆕 Unified launcher
│   ├── pom.xml
│   └── src/.../AgentLauncher.java
│
├── odin-brain/                      ✅ Mantém
│   └── lessons/
│
└── docs/                            📚 Documentation
    ├── ARCHITECTURE.md
    ├── ARCHITECTURE_REVIEW.md       ← Este arquivo!
    ├── AGENT_CREATION_GUIDE.md
    └── LLM_INTEGRATION.md
```

---

## ✅ CHECKLIST DE REFATORAÇÃO

### **FASE 1: Extrair Core** ⏰ 4h
- [ ] Criar módulo `agent-core`
- [ ] Mover `LLMProvider`, `LLMResponse`, `LLMConfig`
- [ ] Mover `PromptTemplate`
- [ ] Criar interface `Agent`
- [ ] Criar classe `BaseAgent`
- [ ] Criar `AgentFactory`
- [ ] Atualizar `odin-agent` para usar `agent-core`
- [ ] Testar build

### **FASE 2: Isolar Providers** ⏰ 3h
- [ ] Criar módulo `llm-providers`
- [ ] Criar submódulo `ollama-provider`
- [ ] Mover `OllamaProvider`
- [ ] Criar factory de providers
- [ ] Atualizar dependências
- [ ] Testar build

### **FASE 3: Refatorar Odin** ⏰ 4h
- [ ] `OdinAgent extends BaseAgent`
- [ ] Injetar `LLMProvider` via construtor
- [ ] Criar `AgentCli` base
- [ ] `OdinCli extends AgentCli`
- [ ] Remover acoplamento direto com Ollama
- [ ] Testar funcionalidade

### **FASE 4: Novo Agente (PoC)** ⏰ 6h
- [ ] Criar `thor-agent`
- [ ] `ThorAgent extends BaseAgent`
- [ ] Implementar lógica específica
- [ ] `ThorCli extends AgentCli`
- [ ] Testar reuso de LLM e Brain
- [ ] Validar arquitetura

### **FASE 5: Launcher** ⏰ 3h
- [ ] Criar `agent-launcher`
- [ ] CLI: `launcher odin|thor|loki`
- [ ] Auto-discovery de agentes
- [ ] Help unificado
- [ ] Testar todos agentes

---

## 🎉 RESULTADO ESPERADO

### **Métricas**
```
Tempo investido:        20h
Agentes suportados:     ♾️  (ilimitado)
Reuso de código:        ~80%
Tempo para novo agente: ~6h
Providers LLM:          Pluggable
```

### **Benefícios**
```
✅ Arquitetura escalável
✅ Código reutilizável
✅ Baixo acoplamento
✅ Alta coesão
✅ Fácil manutenção
✅ Fácil extensão
✅ Testável
```

---

**Pronto para começar a refatoração?** 🚀
