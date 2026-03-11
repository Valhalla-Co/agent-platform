# 🔱 Odin Agent - Agente Especializado em Análise de Código

**Odin** é um agente de IA especializado que analisa projetos de software, aprende seus padrões e gera documentos de especificação (specs) detalhados com sugestões de melhorias.

## 🎯 Filosofia

Diferente de ferramentas CLI tradicionais com múltiplos comandos, **Odin é um agente único e especializado** que:

1. **Aprende continuamente** - Identifica e cataloga padrões arquiteturais, de design e de código
2. **Gera specs estruturadas** - Cria documentos de planejamento profissionais em Markdown
3. **Prioriza inteligentemente** - Categoriza melhorias por prioridade e impacto
4. **Estima realisticamente** - Fornece estimativas de tempo para cada melhoria

## 🚀 Como Usar

### Uso Básico

```bash
# Analisar um projeto
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar <caminho-do-projeto>

# Ou usando o script
.\run.cmd <caminho-do-projeto>

# Exemplo: Analisar o projeto atual
.\run.cmd .
```

### Exemplos

```bash
# Analisar projeto local
.\run.cmd C:\projetos\meu-app

# Analisar projeto atual
.\run.cmd .

# Ver ajuda
java -jar agent-base\target\agent-base-1.0.0-SNAPSHOT.jar --help
```

## 📊 O que Odin Faz

### Fase 1: Descoberta do Projeto
- Identifica tipo de projeto (Maven, Gradle, npm, etc.)
- Mapeia estrutura de diretórios
- Conta arquivos fonte
- Detecta módulos e dependências

### Fase 2: Aprendizado de Padrões
Odin analisa e identifica:

**Padrões Arquiteturais:**
- Hexagonal Architecture (Ports & Adapters)
- Clean Architecture
- Microservices
- Monolítico Modular

**Padrões de Design:**
- Gateway Pattern
- Repository Pattern
- Use Case Pattern
- Factory Pattern
- Builder Pattern

**Padrões de Código:**
- Convenções de nomenclatura
- Estrutura de pacotes
- Uso de frameworks

### Fase 3: Geração de Spec

Odin gera um documento Markdown completo com:

- **Sumário Executivo** - Overview das melhorias
- **Distribuição por Categoria** - Arquitetura, Design, Qualidade, etc.
- **Distribuição por Prioridade** - Alta, Média, Baixa
- **Melhorias Detalhadas** - Cada melhoria inclui:
  - Título e descrição
  - Categoria e prioridade
  - Estimativa de tempo
  - Plano de ação passo a passo
- **Próximos Passos** - Guia para implementação

## 📝 Exemplo de Spec Gerada

```markdown
# 📋 Spec de Melhorias - meu-projeto

**Gerado por:** Odin Agent 🔱
**Data:** 11/03/2026 19:43:58

---

## 📊 Sumário Executivo

- **Total de Melhorias:** 9
- **Prioridade Alta:** 4
- **Estimativa Total:** 118h

### Distribuição por Categoria

- **ARCHITECTURE:** 2
- **DESIGN:** 2
- **CODE_QUALITY:** 2
- **DOCUMENTATION:** 2
- **TESTING:** 2

### 🔴 Alta Prioridade

#### 1. Documentação de Arquitetura

**Categoria:** ARCHITECTURE
**Estimativa:** 8h

**Descrição:**
Criar documento detalhando a arquitetura do projeto, incluindo diagramas e decisões arquiteturais

**Plano de Ação:**
- Criar arquivo docs/architecture.md
- Documentar padrões identificados
- Adicionar diagramas C4 (Context, Container, Component)
- Documentar decisões arquiteturais (ADRs)

---
```

## 🗂️ Estrutura das Specs

As specs são salvas em:
```
<projeto>/odin-specs/improvement-spec-YYYYMMDD_HHMMSS.md
```

Cada execução gera uma nova spec com timestamp, permitindo rastrear a evolução do projeto ao longo do tempo.

## 🎨 Categorias de Melhorias

| Categoria | Descrição |
|-----------|-----------|
| ARCHITECTURE | Decisões arquiteturais, modularização, camadas |
| DESIGN | Padrões de design, estrutura de classes |
| CODE_QUALITY | Code smells, complexidade, duplicação |
| DOCUMENTATION | JavaDoc, READMEs, guias |
| TESTING | Cobertura, testes unitários e integração |
| PERFORMANCE | Otimizações, benchmarks |
| SECURITY | Vulnerabilidades, best practices |

## ⚙️ Configuração

### Requisitos
- Java JDK 21 ou superior
- Maven 3.9+ (incluído via Maven Wrapper)

### Build
```bash
.\mvnw.cmd clean install
```

### Executar
```bash
.\run.cmd <caminho-projeto>
```

## 🧠 Aprendizado Contínuo

Odin foi projetado para **aprender com cada projeto analisado**. Quanto mais projetos analisa, melhor entende:

- Padrões comuns da equipe
- Práticas preferidas
- Estruturas organizacionais
- Decisões arquiteturais recorrentes

## 🔮 Roadmap

- [ ] **Aprendizado Persistente** - Salvar padrões aprendidos entre execuções
- [ ] **Análise Comparativa** - Comparar specs ao longo do tempo
- [ ] **Integração LLM** - Usar LLMs para análises mais profundas
- [ ] **Suporte Multi-Linguagem** - Python, JavaScript, C#, Go
- [ ] **Métricas de Qualidade** - Integrar com SonarQube, Code Climate
- [ ] **Geração de PRs** - Criar pull requests automáticos com melhorias

## 📚 Arquitetura do Odin

```
OdinAgent
├── ProjectDiscovery      → Descobre e mapeia o projeto
├── PatternLearner       → Identifica padrões
│   ├── Architectural
│   ├── Design
│   └── Code
└── SpecGenerator        → Gera specs de melhorias
    ├── Priority Assignment
    ├── Time Estimation
    └── Markdown Generation
```

## 🤝 Contribuindo

Odin é um projeto em evolução. Contribuições são bem-vindas!

### Áreas de Contribuição
- Novos detectores de padrões
- Categorias de melhorias
- Templates de specs
- Suporte a novas linguagens

## 📄 Licença

MIT License - veja [LICENSE](LICENSE)

---

**🔱 Odin Agent** - *Sabedoria através da análise, excelência através da melhoria contínua*
