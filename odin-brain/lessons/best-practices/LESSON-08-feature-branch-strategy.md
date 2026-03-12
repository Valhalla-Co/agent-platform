---
title: Feature Branch Strategy Pattern
category: best-practices
tags: git, branching, workflow, collaboration
applicable_to: All projects
difficulty: beginner
---

# Feature Branch Strategy Pattern

## Overview
Git branching strategy that keeps main branch stable while enabling parallel feature development through isolated branches.

## Key Principles

### Branch Types
```
main                      # Production-ready code
├── feature/name          # New features
├── fix/issue             # Bug fixes
├── refactor/area         # Code refactoring
└── docs/topic            # Documentation updates
```

### Rules
1. **main is sacred**: Always deployable
2. **Branch per feature**: One feature = one branch
3. **Merge via PR**: Never push directly to main
4. **Delete after merge**: Clean up merged branches
5. **Keep branches short-lived**: Max 1-2 weeks

## Detection in Code

Look for:
- Direct commits to main/master
- Long-lived feature branches (>2 weeks)
- Branches without clear naming
- Merged branches not deleted
- Multiple features in one branch

### Git History Issues
```bash
# Bad: Direct commits to main
git log main --oneline
abc123 WIP
def456 fix
ghi789 update

# Good: Merge commits from features
git log main --oneline
abc123 Merge pull request #42 from feature/add-brain
def456 Merge pull request #41 from fix/null-pointer
```

## Improvements to Suggest

### High Priority
1. **Protect main branch**: Enable branch protection
2. **Enforce PR reviews**: Require approvals
3. **Use clear naming**: feature/, fix/, refactor/
4. **Delete merged branches**: Automatic cleanup

### Medium Priority
1. **Branch policies**: CI must pass before merge
2. **Limit branch age**: Flag old branches
3. **Rebase strategy**: Keep history clean
4. **Conventional naming**: Standardize prefixes

### Low Priority
1. **Auto-deployment**: Main deploys automatically
2. **Branch metrics**: Track branch lifecycle
3. **GitFlow/GitHub Flow**: Formal workflow

## Example

### Creating Feature Branch
```bash
# 1. Start from latest main
git checkout main
git pull origin main

# 2. Create feature branch
git checkout -b feature/add-lesson-system

# 3. Work on feature
# ... make changes ...
git add .
git commit -m "feat(brain): Add lesson parser"

# 4. Push to remote
git push -u origin feature/add-lesson-system

# 5. Create PR on GitHub
# ... via web interface ...

# 6. After merge, delete branch
git checkout main
git pull origin main
git branch -d feature/add-lesson-system
git push origin --delete feature/add-lesson-system
```

### Branch Naming Convention
```bash
# Features
feature/add-brain-system
feature/implement-rag
feature/lesson-versioning

# Bug fixes
fix/null-pointer-in-parser
fix/memory-leak
fix/broken-test

# Refactoring
refactor/simplify-discovery
refactor/remove-agent-core
refactor/package-structure

# Documentation
docs/add-brain-guide
docs/update-readme
docs/api-reference
```

## When to Apply

- **Always**: For all project work
- **Team projects**: Essential for collaboration
- **Open source**: Industry standard
- **Solo projects**: Still beneficial for organization

## GitHub Branch Protection

### Settings
```yaml
Branch protection rule: main
├── Require pull request reviews: 1 approval
├── Require status checks: CI must pass
├── Require branches be up to date
├── Restrict who can push
└── Automatically delete head branches
```

### .github/workflows/ci.yml
```yaml
name: CI
on:
  pull_request:
    branches: [main]
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build
        run: mvn clean install
```

## Benefits

✅ Stable main branch  
✅ Parallel development  
✅ Code review process  
✅ Clear feature history  
✅ Easy rollback  
✅ Professional workflow  

## Anti-Patterns to Avoid

❌ Direct commits to main  
❌ Long-lived branches (months)  
❌ Branches without PRs  
❌ Vague branch names ("fix", "update")  
❌ Not deleting merged branches  
❌ Mixing multiple features in one branch  

## Workflow Variations

### GitHub Flow (Simple)
```
main → feature → PR → merge → deploy
```

### GitFlow (Complex)
```
main
├── develop
    ├── feature/
    ├── release/
    └── hotfix/
```

### Trunk-Based (Advanced)
```
main (continuous integration)
└── short-lived feature branches (<1 day)
```

## Related Patterns

- Conventional Commits (LESSON-02)
- PR Body Management (LESSON-01)
- Code Review Checklist (future lesson)

---

**Pattern Difficulty**: Beginner  
**Impact**: High (collaboration)  
**Effort to Apply**: Low (immediate)
