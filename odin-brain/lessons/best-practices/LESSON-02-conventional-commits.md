---
title: Conventional Commits Pattern
category: best-practices
tags: git, commits, version-control, standards
applicable_to: All projects
difficulty: beginner
---

# Conventional Commits Pattern

## Overview
Standardized commit message format that makes commit history readable, enables automated tooling, and improves collaboration.

## Key Principles

### Format Structure
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Commit Types
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation only
- `refactor:` - Code refactoring
- `test:` - Adding/modifying tests
- `chore:` - Maintenance tasks
- `style:` - Code style/formatting
- `perf:` - Performance improvements

### Rules
1. **Type is mandatory**: Every commit must have a type
2. **Subject is concise**: Max 50 characters
3. **Body explains why**: Not what (code shows what)
4. **Footer for breaking changes**: Use `BREAKING CHANGE:`

## Detection in Code

Look for:
- Inconsistent commit messages
- Vague messages like "fix", "update", "changes"
- No pattern in commit history
- Missing context in commits
- Commits mixing multiple concerns

### Good Examples
```
feat(brain): Add lesson-based learning system
fix(cli): Correct argument parsing for project path
docs: Update installation guide with Java 21 requirements
refactor(discovery): Simplify ProjectDiscovery logic
test(brain): Add unit tests for LessonParser
```

### Bad Examples
```
❌ fixed bug
❌ updates
❌ wip
❌ asdfsadf
❌ Final changes
```

## Improvements to Suggest

### High Priority
1. **Adopt conventional commits**: Start using immediately
2. **Add commit linter**: Use commitlint or similar tool
3. **Team training**: Ensure everyone understands pattern

### Medium Priority
1. **Git hooks**: Add commit-msg hook for validation
2. **PR templates**: Include commit message guidelines
3. **Changelog automation**: Use conventional-changelog

### Low Priority
1. **Semantic versioning**: Automate based on commits
2. **Release notes**: Generate from commit messages

## Example

### Complete Commit
```
feat(brain): Add RAG support for semantic lesson search

Implement Retrieval-Augmented Generation to enable semantic
search across lessons. This allows Odin to find relevant 
lessons based on context similarity rather than just tags.

Changes:
- Add EmbeddingService for text vectorization
- Implement semantic similarity calculation
- Update LessonRepository with vector search

BREAKING CHANGE: LessonRepository.find() now requires
EmbeddingService parameter
```

### Scope Examples
```
feat(core): Add new OdinAgent capability
fix(git): Resolve clone timeout issue
docs(brain): Document lesson creation process
refactor(specs): Simplify ImprovementSpec generation
test(discovery): Add ProjectDiscovery integration tests
```

## When to Apply

- **Always**: Every commit in the project
- **Team work**: Essential for collaboration
- **Open source**: Makes contributions clearer
- **Automated releases**: Enables tooling

## Implementation Steps

### 1. Install Commitlint
```bash
npm install --save-dev @commitlint/{cli,config-conventional}
```

### 2. Configure
```js
// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [2, 'always', [
      'feat', 'fix', 'docs', 'refactor', 
      'test', 'chore', 'style', 'perf'
    ]]
  }
};
```

### 3. Add Git Hook
```bash
npx husky add .husky/commit-msg 'npx --no -- commitlint --edit $1'
```

## Benefits

✅ Clear, searchable history  
✅ Automated changelog generation  
✅ Easier code review  
✅ Better collaboration  
✅ Semantic versioning automation  
✅ Quick understanding of changes  

## Anti-Patterns to Avoid

❌ Using multiple types in one commit  
❌ Vague subjects ("fix stuff", "update")  
❌ Missing type prefix  
❌ Commits with unrelated changes  
❌ Not explaining breaking changes  

## Related Patterns

- PR Body Management (LESSON-01)
- Branch Strategy (LESSON-08)
- Code Review Checklist (LESSON-10)

---

**Pattern Difficulty**: Beginner  
**Impact**: High (project maintainability)  
**Effort to Apply**: Low (immediate)
