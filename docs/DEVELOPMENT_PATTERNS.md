# Odin Agent - Development Patterns

Este documento define padrões e regras a serem seguidos durante o desenvolvimento do Odin Agent.

## 📋 Git & Commit Patterns

### PR_BODY.md Rules

**PATTERN:** Sempre que criar `PR_BODY.md`:
1. ✅ Criar o arquivo para uso como referência ao criar PR no GitHub
2. ✅ Usar o conteúdo para copiar/colar na interface do GitHub
3. ✅ **Após aprovação da criação, DELETE o arquivo**
4. ❌ **NUNCA adicione `PR_BODY.md` a um commit**
5. ❌ **NUNCA commite arquivos de mensagem de PR ou commit**

**Motivo:** `PR_BODY.md` é apenas um arquivo auxiliar local, não deve fazer parte do histórico do repositório.

**Ação correta:**
```bash
# Criar PR_BODY.md
# Usar conteúdo para criar PR no GitHub
# Deletar arquivo local
rm PR_BODY.md
# ou
git clean -f PR_BODY.md
```

---

## 📁 Documentation Patterns

### Documentation Structure

**PATTERN:** Toda documentação deve estar em `docs/`
- ✅ Arquivos `.md` de documentação → `docs/`
- ✅ Índice de documentação → `docs/README.md`
- ✅ Raiz do projeto deve conter apenas: README.md, LICENSE, pom.xml, scripts de build
- ❌ Não deixar múltiplos `.md` na raiz

---

## 💬 Commit Message Patterns

### Conventional Commits

**PATTERN:** Use conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat:` - Nova funcionalidade
- `fix:` - Correção de bug
- `docs:` - Apenas documentação
- `refactor:` - Refatoração de código
- `test:` - Adição/modificação de testes
- `chore:` - Tarefas de manutenção

**Exemplos:**
```
feat(brain): Add lesson-based learning system
docs: Add comprehensive Odin Brain guide
refactor: Organize documentation into docs/ folder
fix(cli): Correct argument parsing
```

---

## 🗂️ File Naming Patterns

### Temporary/Auxiliary Files

**PATTERN:** Arquivos temporários ou auxiliares:
- `*_BODY.md` - Nunca commitar
- `*_TEMP.md` - Nunca commitar
- `*_DRAFT.md` - Nunca commitar
- `*.tmp` - Nunca commitar

**Adicionar ao `.gitignore`:**
```gitignore
# Auxiliary files
*_BODY.md
*_TEMP.md
*_DRAFT.md
*.tmp
```

---

## 📝 Lesson Patterns

### Creating New Lessons

**PATTERN:** Formato de lesson em `odin-brain/lessons/`:

```markdown
---
title: Lesson Title
category: architecture|patterns|best-practices
tags: tag1, tag2, tag3
applicable_to: Java, Python, etc
difficulty: beginner|intermediate|advanced
---

# Lesson Title

## Overview
Brief description

## Key Concepts
- Concept 1
- Concept 2

## Detection in Code
How to identify in code

## Improvements to Suggest
What to recommend

## Example
Code examples
```

**Naming:** Use kebab-case: `my-lesson-name.md`

---

## 🔧 Code Patterns

### Verbosity

**PATTERN:** Minimal output
- ✅ 2 linhas principais: "Analyzing: X" e "Spec: Y (N improvements)"
- ❌ Evitar decorações excessivas (caixas, emojis)
- ❌ Evitar múltiplas linhas de progresso detalhado

**Para logs detalhados:** Considerar flag `--verbose` no futuro

### Package Structure

**PATTERN:** Estrutura de pacotes simples e direta:
```
br.com.valhalla.odin/
├── core/          # Lógica principal
├── brain/         # Sistema de aprendizado
├── git/           # Operações Git
├── discovery/     # Descoberta de projetos
├── patterns/      # Padrões identificados
└── specs/         # Especificações de melhoria
```

---

## 🧪 Testing Patterns

### Test Organization

**PATTERN:** Testes devem espelhar estrutura de src:
```
src/test/java/br/com/valhalla/odin/
├── core/
│   └── OdinAgentTest.java
├── brain/
│   └── LessonParserTest.java
└── ...
```

---

## 🚀 Release Patterns

### Version Numbering

**PATTERN:** Semantic Versioning (SemVer)
- `MAJOR.MINOR.PATCH`
- `1.0.0` - Release inicial
- `1.1.0` - Nova funcionalidade (backward compatible)
- `1.0.1` - Bug fix
- `2.0.0` - Breaking changes

---

## 📊 Metrics Patterns

### Code Quality

**PATTERN:** Manter métricas:
- Classes por módulo: < 20
- Linhas por classe: < 300
- Métodos por classe: < 15
- Linhas por método: < 50
- Complexidade ciclomática: < 10

---

## 🔄 Workflow Patterns

### Branch Strategy

**PATTERN:** Feature branches
```
main                    # Produção estável
└── feature/name        # Nova funcionalidade
└── fix/issue           # Correção
└── refactor/area       # Refatoração
```

**Merge:** Sempre via Pull Request com review

---

## 📞 Communication Patterns

### Issue Naming

**PATTERN:** Títulos descritivos
```
[Feature] Add RAG support to lesson system
[Bug] Fix NPE in ProjectDiscovery
[Docs] Update installation guide
[Refactor] Simplify SpecGenerator logic
```

---

## 🎯 Code Review Patterns

### Review Checklist

**PATTERN:** Verificar em todo PR:
- [ ] Código compila sem erros
- [ ] Testes passam
- [ ] Documentação atualizada
- [ ] Sem arquivos temporários commitados (`*_BODY.md`, etc)
- [ ] Commit messages seguem padrão
- [ ] Código segue estilo do projeto

---

## 🔒 Security Patterns

### Sensitive Data

**PATTERN:** Nunca commitar:
- ❌ Tokens de API (GITHUB_TOKEN, etc)
- ❌ Passwords
- ❌ Private keys
- ❌ Configurações locais com dados sensíveis

**Usar:** Variáveis de ambiente ou arquivos de config local não versionados

---

## 📚 Documentation Maintenance

### Keeping Docs Updated

**PATTERN:** Ao adicionar feature:
1. Atualizar README.md principal
2. Atualizar docs relevantes em `docs/`
3. Adicionar exemplos de uso
4. Atualizar CHANGELOG (se existir)

---

**Este documento deve ser consultado antes de commits significativos e ao criar PRs.**

**Última atualização:** 11/03/2026
