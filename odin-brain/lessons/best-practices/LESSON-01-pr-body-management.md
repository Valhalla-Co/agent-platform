---
title: PR Body Management Pattern
category: best-practices
tags: git, documentation, pr, workflow
applicable_to: All projects
difficulty: beginner
---

# PR Body Management Pattern

## Overview
Pattern for managing PR_BODY.md files - temporary auxiliary files used to create Pull Request descriptions that should never be committed to the repository.

## Key Principles

### The Rule
**NEVER commit PR_BODY.md or similar auxiliary files to version control.**

### Workflow
1. ✅ Create `PR_BODY.md` as local reference
2. ✅ Use content to copy/paste into GitHub PR interface
3. ✅ **Delete the file immediately after creating PR**
4. ❌ **NEVER add to git commits**

### Why This Matters
- PR body content belongs in GitHub, not in repository history
- Keeps repository clean and focused on code
- Auxiliary files are meant to be temporary tools
- Prevents cluttering git history with metadata

## Detection in Code

Look for:
- Files ending in `*_BODY.md`
- Files ending in `*_FINAL.md`
- Files ending in `*_TEMP.md` or `*_DRAFT.md`
- Auxiliary markdown files in root directory
- Files that should be in `.gitignore` but aren't

## Improvements to Suggest

### High Priority
1. **Add to .gitignore**: Ensure auxiliary files are excluded
2. **Delete existing PR_BODY.md**: Remove if found in repository
3. **Document the pattern**: Add to project's development guidelines

### Medium Priority
1. **Create templates**: Use GitHub PR templates instead
2. **Automate cleanup**: Add pre-commit hook to prevent commits

## Example

### Correct Workflow
```bash
# 1. Create PR body locally
cat > PR_BODY.md << EOF
# Feature: Add new capability
Description...
EOF

# 2. Copy content and create PR on GitHub
# (use GitHub web interface or gh cli)

# 3. Delete the file
rm PR_BODY.md

# 4. Verify it's not tracked
git status  # Should not show PR_BODY.md
```

### .gitignore Configuration
```gitignore
# Auxiliary files (never commit)
*_BODY.md
*_TEMP.md
*_DRAFT.md
*.tmp
```

### Pre-commit Hook (Optional)
```bash
#!/bin/bash
# .git/hooks/pre-commit

if git diff --cached --name-only | grep -E "*_BODY.md|*_TEMP.md|*_DRAFT.md"; then
    echo "Error: Attempting to commit auxiliary files"
    echo "Please remove *_BODY.md, *_TEMP.md, or *_DRAFT.md files"
    exit 1
fi
```

## When to Apply

- **Always**: When creating PRs manually
- **Any project**: Regardless of language or framework
- **Team workflows**: Especially important in collaborative environments
- **CI/CD pipelines**: Prevents build artifacts in repository

## Anti-Patterns to Avoid

❌ Committing PR_BODY.md "just this once"  
❌ Keeping auxiliary files "for reference"  
❌ Not adding to .gitignore  
❌ Ignoring the pattern because "it's just one file"

## Benefits

✅ Clean repository history  
✅ Focus on actual code and documentation  
✅ Prevents confusion about file purpose  
✅ Easier code reviews  
✅ Better project organization  

## Related Patterns

- Conventional Commits (LESSON-02)
- Documentation Structure (LESSON-03)
- File Naming Conventions (LESSON-04)

---

**Pattern Difficulty**: Beginner  
**Impact**: High (repository cleanliness)  
**Effort to Apply**: Low (1 minute)
