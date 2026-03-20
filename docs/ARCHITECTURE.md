# 🏛️ Architecture Documentation

**Status:** Current (v2.0 - Unification)  
**Last Updated:** 2026-03-20  

---

## 🎯 Overview

The **Agent Platform** is a modular, multi-agent system designed to automate software development tasks. It uses a centralized orchestrator (**Odin**) to delegate specialized tasks to expert agents (**Wayland** for dev, **Mimir** for architecture), all powered by interchangeable LLM providers.

## 🏗️ High-Level Architecture

The system follows a Hexagonal/Clean Architecture principle where the Core logic is isolated from external frameworks and the Orchestrator acts as the primary driving adapter.

```mermaid
graph TD
    User((User)) -->|CLI / HTTP| Orchestrator[Odin Orchestrator]
    
    subgraph "Agent Platform"
        Orchestrator -->|Delegates| DevAgent[Dev Agent <br/>(Wayland)]
        Orchestrator -->|Delegates| ArchAgent[Architect Agent <br/>(Mimir)]
        
        DevAgent -->|Uses| Core[Agent Core]
        ArchAgent -->|Uses| Core
        Orchestrator -->|Uses| Core
        
        Core -->|Defines Interface| LLM_SPI[[LLM Provider SPI]]
        Core -->|Defines Interface| Brain_SPI[[Knowledge Brain]]
    end
    
    subgraph "External Integrations"
        Ollama[Ollama Provider] -.->|Implements| LLM_SPI
        OpenAI[OpenAI Provider] -.->|Implements| LLM_SPI
        
        FS[(File System)] -.->|Stores| Brain_SPI
    end
```

---

## 📦 Module Structure

The project is structured as a Maven multi-module reactor:

### 1. **Core & Infrastructure**
| Module | Description | Dependencies |
|--------|-------------|--------------|
| `agent-core` | Shared domain logic, interfaces (LLM, Agent, Tool), and common utilities. Contains the **Brain** logic (Lesson/KnowledgeBase). | None (Java SE) |
| `llm-providers/*` | Concrete implementations of the LLM interfaces defined in core. | `agent-core` |

### 2. **Orchestration**
| Module | Module Name | Description |
|--------|-------------|-------------|
| `odin-orchestrator` | **Entry Point**. CLI/Runner application. Handles user input, environment setup, and dispatches commands to agents. | `agent-core`, Agents, Providers |

### 3. **Specialized Agents**
| Module | Agent Name | Persona | Responsibility |
|--------|------------|---------|----------------|
| `dev-agent` | **Wayland** | Implementer | Code generation, refactoring, fixing unit tests, running builds. |
| `architect-agent` | **Mimir** | Analyzer | Architecture review, documentation generation, RFC creation, pattern analysis. |

---

## 🧠 Key Concepts

### Agent Brain (Knowledge Base)
Examples and learnings are stored as Markdown files in the filesystem. This allows agents to "learn" by simply adding new documentation files.
- **Lessons**: Structured examples of "Task -> Thought Process -> Solution".
- **Patterns**: Reusable code snippets and architectural rules.

### Workspace Isolation
Agents operate within specific **Workspaces** to prevent accidental overrides.
- `agent-workspace/`: Default sandbox for temporary file generation.
- `branches/`: (Planned) Directory for checking out external repos for modification.

### LLM Abstraction
The system is agnostic to the LLM backend.
- **Interface**: `br.com.valhalla.core.llm.LLMProvider`
- **Configuration**: Managed via `pom.xml` or environment variables (e.g., `OLLAMA_API_URL`).
- **Current Default**: Ollama (local) running `codellama` or `mistral`.

---

## 🔄 Execution Flow

1. **Bootstrap**: `run-odin.ps1` builds the project and launches `odin-orchestrator`.
2. **Initialization**: Odin scans for available agents and LLM providers.
3. **Dispatch**:
   - User command: *"Fix the integration test error in UserService"*
   - Odin identifies intent -> Routes to **Dev Agent (Wayland)**.
4. **Execution**:
   - **Wayland** analyzes the request.
   - Queries **Brain** for similar past errors.
   - Constructs a prompt for the **LLM Provider**.
   - Receives code/patch.
   - Validates using **Agent Core** utilities (e.g., running a test).
5. **Result**: Orchestrator presents the result to the user or applies the patch.

---

## 🛠️ Development & Extension

### Adding a New Agent
1. Create a new maven module (e.g., `qua-agent`).
2. Extend `BaseAgent` from `agent-core`.
3. Define the specific capabilities and system prompt (Persona).
4. Register in `odin-orchestrator`.

### Adding an LLM Provider
1. Create a module under `llm-providers/`.
2. Implement `LLMProvider` interface.
3. Handle API communication (HTTP REST / SDK).
