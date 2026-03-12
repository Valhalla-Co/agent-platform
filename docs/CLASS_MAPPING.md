# 📋 Mapeamento de Classes - Reestruturação Odin Agent

**Data:** 11/03/2026  
**Status:** Backup antes da reestruturação

---

## 📦 Estrutura Atual

### agent-core/

#### domain/
- `Agent.java` - ❌ Remover
- `AgentConversation.java` - ❌ Remover
- `AgentMessage.java` - ❌ Remover
- `AgentStatus.java` - 🔄 Simplificar → `ProjectStatus.java`
- `AgentType.java` - ❌ Remover
- `Task.java` - ✅ Já migrado para `Improvement.java`
- `TaskPriority.java` - ✅ Já migrado para `ImprovementPriority.java`
- `TaskStatus.java` - ❌ Remover
- `TaskType.java` - ❌ Remover

#### dto/
- `AgentMessageDTO.java` - ❌ Remover
- `AgentTaskResult.java` - ❌ Remover
- `ArchitectRequest.java` - ❌ Remover
- `JavaEngineerRequest.java` - ❌ Remover
- `OrchestrationContext.java` - ❌ Remover
- `OrchestrationTaskRequest.java` - ❌ Remover
- `OrchestrationTaskResponse.java` - ❌ Remover
- `TaskRequest.java` - ❌ Remover
- `TaskResponse.java` - ❌ Remover

#### ports/
- `AgentCommunicationPort.java` - ❌ Remover (ports não são necessários)
- `AgentGateway.java` - ❌ Remover
- `AgentLLMPort.java` - ❌ Remover
- `EventPublisher.java` - ❌ Remover
- `LLMPort.java` - ❌ Remover
- `TaskExecutionPort.java` - ❌ Remover

---

### agent-base/

#### Manter e Migrar para odin-agent:

**agentbase/odin/ → odin/core/**
- ✅ `OdinAgent.java` → `br.com.valhalla.odin.core.OdinAgent`
- ✅ `PatternLearner.java` → `br.com.valhalla.odin.core.PatternLearner`
- ✅ `SpecGenerator.java` → `br.com.valhalla.odin.core.SpecGenerator`
- ✅ `OdinAnalysisResult.java` → `br.com.valhalla.odin.core.OdinAnalysisResult`
- ✅ `LearnedPatterns.java` → `br.com.valhalla.odin.patterns.LearnedPatterns`
- ✅ `ImprovementSpec.java` → `br.com.valhalla.odin.specs.ImprovementSpec`
- ✅ `Improvement.java` → `br.com.valhalla.odin.specs.Improvement`
- ✅ `ImprovementPriority.java` → `br.com.valhalla.odin.specs.ImprovementPriority`
- ✅ `ImprovementCategory.java` → `br.com.valhalla.odin.specs.ImprovementCategory`

**agentbase/discovery/ → odin/discovery/**
- ✅ `ProjectDiscovery.java` → `br.com.valhalla.odin.discovery.ProjectDiscovery`
- ✅ `ProjectContext.java` → `br.com.valhalla.odin.model.Project`

**agentbase/ (root)**
- 🔄 `AgentBaseCli.java` → `br.com.valhalla.odin.OdinCli`

#### Remover Completamente:

**agentbase/cli/**
- ❌ `AnalyzeCommand.java`
- ❌ `ImproveCommand.java`
- ❌ `ReviewCommand.java`

**agentbase/orchestration/**
- ❌ `CancelTaskUseCase.java`
- ❌ `CoordinateAgentCommunicationUseCase.java`
- ❌ `DelegateToArchitectUseCase.java`
- ❌ `DelegateToJavaEngineerUseCase.java`
- ❌ `MonitorTaskProgressUseCase.java`
- ❌ `OrchestrateTaskUseCase.java`

**agentbase/gateway/**
- ❌ `LocalFileSystemGateway.java`

**agentbase/ (root)**
- ❌ `CreateTaskCommand.java`
- ❌ `RestAgentGateway.java`
- ❌ `RootCommand.java`

---

## 🔄 Plano de Migração por Pacote

### 1. br.com.valhalla.odin.core/
**Origem:** `br.com.valhalla.agentbase.odin.*`

| Classe Origem | Classe Destino | Status |
|---------------|----------------|--------|
| `OdinAgent.java` | `OdinAgent.java` | 🔄 Atualizar imports |
| `PatternLearner.java` | `PatternLearner.java` | 🔄 Atualizar imports |
| `SpecGenerator.java` | `SpecGenerator.java` | 🔄 Atualizar imports |
| `OdinAnalysisResult.java` | `OdinAnalysisResult.java` | 🔄 Atualizar imports |

### 2. br.com.valhalla.odin.discovery/
**Origem:** `br.com.valhalla.agentbase.discovery.*`

| Classe Origem | Classe Destino | Status |
|---------------|----------------|--------|
| `ProjectDiscovery.java` | `ProjectDiscovery.java` | 🔄 Atualizar imports |
| `ProjectContext.java` | Migrar para `model/` | 🔄 Mover |

### 3. br.com.valhalla.odin.patterns/
**Origem:** `br.com.valhalla.agentbase.odin.*`

| Classe Origem | Classe Destino | Status |
|---------------|----------------|--------|
| `LearnedPatterns.java` | `LearnedPatterns.java` | 🔄 Mover e atualizar |

### 4. br.com.valhalla.odin.specs/
**Origem:** `br.com.valhalla.agentbase.odin.*`

| Classe Origem | Classe Destino | Status |
|---------------|----------------|--------|
| `ImprovementSpec.java` | `ImprovementSpec.java` | 🔄 Mover e atualizar |
| `Improvement.java` | `Improvement.java` | 🔄 Mover e atualizar |
| `ImprovementPriority.java` | `ImprovementPriority.java` | 🔄 Mover e atualizar |
| `ImprovementCategory.java` | `ImprovementCategory.java` | 🔄 Mover e atualizar |

### 5. br.com.valhalla.odin.model/
**Novo pacote - Modelos POJO simples**

| Classe | Origem | Status |
|--------|--------|--------|
| `Project.java` | `ProjectContext.java` | 🆕 Criar simplificado |
| `ProjectType.java` | `ProjectContext.ProjectType` | 🆕 Extrair enum |
| `AnalysisResult.java` | - | 🆕 Criar novo |

---

## 📊 Estatísticas de Classes

### Total de Classes
- **agent-core:** 38 classes
  - ✅ Manter: 0
  - 🔄 Migrar: 2 (simplificadas)
  - ❌ Remover: 36

- **agent-base:** 25 classes
  - ✅ Manter: 9
  - 🔄 Migrar: 10
  - ❌ Remover: 6

### Resumo Final
- **Total Atual:** 63 classes
- **Total Após Reestruturação:** ~15 classes
- **Redução:** 76% menos classes!

---

## 🎯 Dependências

### Remover do pom.xml:
- ❌ Dependência `agent-core`
- ❌ Módulo `agent-core` do parent

### Manter:
- ✅ picocli (CLI)
- ✅ jackson (JSON)
- ✅ slf4j (logging)
- ✅ junit (testes)

---

## ⚠️ Pontos de Atenção

### 1. Imports a Atualizar
Todos os imports devem mudar de:
```java
import br.com.valhalla.agentbase.odin.*;
import br.com.valhalla.agentbase.discovery.*;
import br.com.valhalla.agentcore.*;
```

Para:
```java
import br.com.valhalla.odin.core.*;
import br.com.valhalla.odin.discovery.*;
import br.com.valhalla.odin.model.*;
import br.com.valhalla.odin.patterns.*;
import br.com.valhalla.odin.specs.*;
```

### 2. Testes a Atualizar
- `CreateTaskCommandTest.java` - ❌ Remover
- `RestAgentGatewayTest.java` - ❌ Remover
- `RestAgentGatewayNegativeTest.java` - ❌ Remover
- Criar novos testes para `OdinAgent`

### 3. Scripts a Atualizar
- `run.cmd` - Atualizar caminho do JAR
- `build.cmd` - Atualizar referências

---

## ✅ Checklist de Verificação

### Antes de Começar
- [x] Branch criada: `feature/architecture-unification`
- [x] Commit do estado atual
- [x] Mapeamento de classes documentado

### Durante a Migração
- [ ] Backup de classes importantes
- [ ] Testar compilação após cada fase
- [ ] Validar Odin funcionando

### Após Conclusão
- [ ] Build SUCCESS
- [ ] Todos os testes passando
- [ ] Spec sendo gerada corretamente
- [ ] Documentação atualizada

---

**🔱 Documento criado automaticamente durante Fase 1: Preparação e Backup**

*Este documento serve como referência durante todo o processo de reestruturação*
