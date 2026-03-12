---
title: File Naming Conventions Pattern
category: best-practices
tags: naming, conventions, organization, files
applicable_to: All projects
difficulty: beginner
---

# File Naming Conventions Pattern

## Overview
Consistent file naming conventions that make project files easy to identify, understand, and organize.

## Key Principles

### Convention Categories

#### 1. Temporary/Auxiliary Files
```
*_BODY.md     - PR/issue body templates (NEVER commit)
*_TEMP.md     - Temporary files (NEVER commit)
*_DRAFT.md    - Draft documents (NEVER commit)
*.tmp         - Temporary files (NEVER commit)
```

#### 2. Lesson Files
```
LESSON-##-descriptive-name.md
Example: LESSON-01-pr-body-management.md
```

#### 3. Documentation Files
```
UPPERCASE_NAME.md for major docs
lowercase-kebab-case.md for regular docs

Examples:
- README.md
- CONTRIBUTING.md
- setup-guide.md
- api-reference.md
```

#### 4. Code Files
```
PascalCase for classes: OdinAgent.java
camelCase for methods: cloneRepository()
kebab-case for scripts: build-project.sh
```

## Detection in Code

Look for:
- Inconsistent naming patterns
- Mixed case styles (snake_case, camelCase, kebab-case)
- Vague names (temp.md, file1.java, test.md)
- Files without extensions
- Special characters in names

### Bad Examples
```
❌ pr body.md (spaces)
❌ TEMP_FILE.MD (wrong pattern)
❌ myFile.md (inconsistent)
❌ file1, file2 (meaningless)
❌ new-feature_FINAL.md (mixed styles)
```

### Good Examples
```
✅ PR_BODY.md (auxiliary, in .gitignore)
✅ LESSON-05-naming-conventions.md (lesson)
✅ CONTRIBUTING.md (major doc)
✅ setup-guide.md (regular doc)
✅ OdinAgent.java (class)
```

## Improvements to Suggest

### High Priority
1. **Standardize lesson names**: Use LESSON-## prefix
2. **Add to .gitignore**: Exclude temporary file patterns
3. **Rename inconsistent files**: Fix naming violations
4. **Document conventions**: Add to project guidelines

### Medium Priority
1. **Validate in CI**: Add filename checks to CI pipeline
2. **Pre-commit hook**: Prevent bad filenames
3. **Migration guide**: Help team adopt conventions

### Low Priority
1. **Automated rename**: Script to fix all files
2. **Editor config**: Configure IDE naming rules

## Example

### .gitignore Configuration
```gitignore
# Temporary and auxiliary files
*_BODY.md
*_TEMP.md
*_DRAFT.md
*.tmp
*.bak
*~

# Editor artifacts
.vscode/
.idea/
*.swp
```

### Project Structure Example
```
project/
├── README.md                    # Main readme
├── CONTRIBUTING.md              # Contribution guide
├── LICENSE                      # License file
├── pom.xml                      # Build config
│
├── docs/
│   ├── README.md                # Doc index
│   ├── setup-guide.md           # Setup doc
│   ├── api-reference.md         # API doc
│   └── DEVELOPMENT_PATTERNS.md  # Major doc
│
├── odin-brain/
│   └── lessons/
│       ├── LESSON-01-pr-body-management.md
│       ├── LESSON-02-conventional-commits.md
│       └── LESSON-03-documentation-structure.md
│
└── src/
    └── main/
        └── java/
            └── OdinAgent.java   # PascalCase class
```

### Naming Decision Tree
```
Is it a lesson?
  ├─ Yes → LESSON-##-kebab-case.md
  └─ No
      ├─ Major documentation?
      │   ├─ Yes → UPPERCASE_NAME.md
      │   └─ No → lowercase-kebab-case.md
      │
      ├─ Temporary file?
      │   └─ Yes → *_TEMP.md (and add to .gitignore)
      │
      └─ Code file?
          ├─ Class → PascalCase.java
          ├─ Script → kebab-case.sh
          └─ Config → lowercase.yml
```

## When to Apply

- **Project setup**: Define conventions early
- **Onboarding**: Teach new team members
- **Code review**: Enforce during reviews
- **Refactoring**: Fix when cleaning up

## Benefits

✅ Instant file purpose recognition  
✅ Better organization  
✅ Easier searching  
✅ Professional appearance  
✅ Tool compatibility  
✅ Clear separation of concerns  

## Anti-Patterns to Avoid

❌ Mixing naming styles in same directory  
❌ Using spaces in filenames  
❌ Vague names (file1, temp, new)  
❌ Not documenting conventions  
❌ Inconsistent lesson numbering  

## Automation Example

### Pre-commit Hook
```bash
#!/bin/bash
# .git/hooks/pre-commit

# Check for invalid filenames
if git diff --cached --name-only | grep -E " "; then
    echo "Error: Filenames with spaces detected"
    exit 1
fi

# Check for uncommitted temporary files
if git diff --cached --name-only | grep -E "_TEMP|_DRAFT|_BODY"; then
    echo "Error: Temporary files should not be committed"
    exit 1
fi
```

## Related Patterns

- PR Body Management (LESSON-01)
- Documentation Structure (LESSON-03)
- Lesson Creation (LESSON-05)

---

**Pattern Difficulty**: Beginner  
**Impact**: Medium (project organization)  
**Effort to Apply**: Low (immediate for new files)
