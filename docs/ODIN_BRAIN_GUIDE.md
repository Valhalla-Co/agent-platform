# Odin Brain & Repository Management - Quick Start

## Overview

O Odin Agent agora possui um "cérebro" (brain) que aprende através de lessons em Markdown e pode clonar/gerenciar repositórios da organização Valhalla-Co para aplicar melhorias.

---

## Estrutura

```
odin-brain/
├── lessons/
│   ├── architecture/
│   │   └── hexagonal-architecture.md
│   ├── patterns/
│   │   └── repository-pattern.md
│   └── best-practices/
│       └── clean-code-principles.md
└── README.md

workspace/
└── branches/
    ├── repo1/  (cloned from Valhalla-Co)
    ├── repo2/
    └── repo3/
```

---

## Usando o Brain

### 1. Carregar Lessons

```java
OdinBrain brain = new OdinBrain();
brain.initialize();
// Output: Brain loaded: 3 lessons
```

### 2. Listar Lessons

```java
// Todas as lessons
List<Lesson> all = brain.getAllLessons();

// Por categoria
List<Lesson> architectureLessons = brain.getLessonsByCategory("architecture");
List<Lesson> patternLessons = brain.getLessonsByCategory("patterns");

// Resumo
String summary = brain.getKnowledgeSummary();
// Output: "3 lessons in 3 categories: architecture, patterns, best-practices"
```

### 3. Aprender com Contexto do Projeto

```java
ProjectContext context = discovery.discover("path/to/project");
List<String> insights = brain.learn(context);
// Retorna insights relevantes baseados em lessons
```

---

## Gerenciando Repositórios

### 1. Clonar Repositório

```java
OdinBrain brain = new OdinBrain();

// Clone da organização Valhalla-Co
Path repoPath = brain.cloneRepository("my-service");
// Clonado em: workspace/branches/my-service

// Clone em branch específica
Path repoPath = brain.cloneRepository("my-service", "develop");
```

### 2. Listar Repositórios no Workspace

```java
List<String> repos = brain.listWorkspaceRepositories();
repos.forEach(System.out::println);
```

### 3. Aplicar Conhecimento em Repositório

```java
// Clona o repo e cria branch para melhorias
Path repoPath = brain.applyKnowledgeToRepository("target-service");

// Output:
// Cloning: Valhalla-Co/target-service
// Cloned: workspace/branches/target-service
// Created branch: odin/improvements-1710187890123
```

### 4. Operações Git Avançadas

```java
RepositoryManager repoMgr = brain.getRepositoryManager();

// Criar branch
repoMgr.createBranch(repoPath, "feature/odin-improvements");

// Commit
repoMgr.commit(repoPath, "Apply Odin improvements");

// Push
repoMgr.push(repoPath, "feature/odin-improvements");
```

---

## Configuração

### Variáveis de Ambiente (Opcional)

```bash
# Organização GitHub (padrão: Valhalla-Co)
export ODIN_GIT_ORG=Valhalla-Co

# Diretório de workspace (padrão: workspace/branches)
export ODIN_WORKSPACE=~/odin-workspace

# Token GitHub para repos privados (opcional)
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxx
```

### Windows (PowerShell)

```powershell
$env:ODIN_GIT_ORG = "Valhalla-Co"
$env:ODIN_WORKSPACE = "C:\workspace\odin"
$env:GITHUB_TOKEN = "ghp_xxxxxxxxxxxxx"
```

---

## Criando Novas Lessons

### Formato da Lesson

```markdown
---
title: My Lesson Title
category: architecture|patterns|best-practices
tags: tag1, tag2, tag3
applicable_to: Java, Spring, etc
difficulty: beginner|intermediate|advanced
---

# Lesson Title

## Overview
Brief description...

## Key Concepts
- Point 1
- Point 2

## Detection in Code
How to identify this in code...

## Improvements to Suggest
What to recommend...

## Example
Before/after code examples...
```

### Adicionar Nova Lesson

1. Escolha a categoria (architecture/patterns/best-practices)
2. Crie arquivo `.md` em `odin-brain/lessons/<category>/`
3. Use formato acima com frontmatter YAML
4. Inclua exemplos práticos de código

---

## Exemplo Completo de Uso

### Análise de Projeto com Knowledge

```java
public class OdinExample {
    public static void main(String[] args) {
        // 1. Inicializar brain
        OdinBrain brain = new OdinBrain();
        brain.initialize();
        System.out.println(brain.getKnowledgeSummary());
        
        // 2. Analisar projeto local
        OdinAgent odin = new OdinAgent();
        Path localProject = Paths.get(".");
        OdinAnalysisResult result = odin.analyze(localProject);
        
        // 3. Aprender insights do projeto
        ProjectContext context = result.getContext();
        List<String> insights = brain.learn(context);
        insights.forEach(System.out::println);
    }
}
```

### Clonar e Melhorar Repositório Externo

```java
public class OdinRepoImprover {
    public static void main(String[] args) throws IOException {
        OdinBrain brain = new OdinBrain();
        
        // 1. Clonar repositório da organização
        String repoName = "example-service";
        Path repoPath = brain.applyKnowledgeToRepository(repoName);
        // Repo clonado em: workspace/branches/example-service
        // Branch criada: odin/improvements-<timestamp>
        
        // 2. Analisar o repositório clonado
        OdinAgent odin = new OdinAgent();
        OdinAnalysisResult result = odin.analyze(repoPath);
        
        // 3. Spec gerada automaticamente
        System.out.println("Spec: " + result.getSpecPath());
        
        // 4. (Futuro) Aplicar melhorias automaticamente
        // brain.applyImprovements(repoPath, result.getSpec());
        
        // 5. Fazer commit e push
        RepositoryManager repoMgr = brain.getRepositoryManager();
        repoMgr.commit(repoPath, "Add Odin improvement spec");
        // repoMgr.push(repoPath, "odin/improvements-<timestamp>");
    }
}
```

---

## Fluxo de Trabalho Recomendado

### 1. Para Projetos Locais

```
Odin Analyze → Learn from Lessons → Generate Spec → Apply Manually
```

### 2. Para Repositórios da Organização

```
Clone Repo → Create Branch → Analyze → Generate Spec → 
Commit Changes → Push → Create PR
```

---

## Estrutura de Workspace

Depois de clonar repositórios:

```
workspace/
└── branches/
    ├── service-a/
    │   ├── .git/
    │   ├── src/
    │   └── odin-specs/
    │       └── improvement-spec-xxx.md
    ├── service-b/
    └── library-x/
```

---

## Próximos Passos

- [ ] Integrar LLM para extrair insights mais profundos das lessons
- [ ] RAG (Retrieval-Augmented Generation) para busca semântica
- [ ] Auto-aplicação de melhorias simples
- [ ] Geração automática de PRs
- [ ] Métricas de efetividade das lessons
- [ ] Sistema de contribuição de lessons pela comunidade

---

## Troubleshooting

### "Brain loaded: 0 lessons"

- Verifique se `odin-brain/lessons/` existe
- Verifique se há arquivos `.md` nas subpastas

### "Failed to clone repository"

- Verifique conexão com GitHub
- Para repos privados, configure `GITHUB_TOKEN`
- Verifique se a organização está correta

### Workspace não criado

- Verifique permissões de escrita
- Configure `ODIN_WORKSPACE` se quiser local diferente

---

**O Odin agora aprende continuamente e pode aplicar conhecimento em múltiplos projetos!**
