# Odin Brain - Knowledge System

## Overview

The Odin Brain is a knowledge management system that allows Odin Agent to learn from structured lessons and apply that knowledge when analyzing and improving projects.

## Structure

```
odin-brain/
├── lessons/                    # Markdown lessons organized by category
│   ├── architecture/          # Architecture patterns and principles
│   ├── patterns/              # Design patterns
│   └── best-practices/        # Coding best practices
└── README.md                  # This file
```

## Lesson Format

Lessons are Markdown files with YAML frontmatter metadata:

```markdown
---
title: Lesson Title
category: architecture|patterns|best-practices
tags: tag1, tag2, tag3
applicable_to: Java, Python, etc
difficulty: beginner|intermediate|advanced
---

# Lesson Content

Content in Markdown format...
```

### Metadata Fields

- **title**: Lesson name
- **category**: Primary category (matches directory structure)
- **tags**: Comma-separated tags for filtering
- **applicable_to**: Languages/frameworks where this applies
- **difficulty**: Complexity level

## How It Works

### 1. Loading Lessons

```java
OdinBrain brain = new OdinBrain();
brain.initialize(); // Loads all lessons from odin-brain/lessons/
```

### 2. Finding Relevant Lessons

```java
List<Lesson> relevant = brain.learn(projectContext);
// Returns lessons applicable to the project type and language
```

### 3. Applying Knowledge

The brain provides insights based on loaded lessons that influence:
- Pattern detection
- Improvement suggestions
- Priority assignment
- Best practice recommendations

## Repository Management

The brain can also clone and manage repositories from Valhalla-Co organization:

### Clone Repository

```java
OdinBrain brain = new OdinBrain();
Path repoPath = brain.cloneRepository("my-project");
```

### Apply Knowledge to Repository

```java
// Clones repo and creates improvement branch
Path repoPath = brain.applyKnowledgeToRepository("my-project");
```

### List Workspace Repositories

```java
List<String> repos = brain.listWorkspaceRepositories();
```

## Workspace Structure

Cloned repositories are stored in:
```
workspace/
└── branches/
    ├── repo1/
    ├── repo2/
    └── repo3/
```

## Configuration

### Environment Variables

- `ODIN_GIT_ORG` - GitHub organization (default: Valhalla-Co)
- `ODIN_WORKSPACE` - Workspace path (default: workspace/branches)
- `GITHUB_TOKEN` - GitHub token for private repos (optional)

### Example

```bash
export ODIN_GIT_ORG=Valhalla-Co
export ODIN_WORKSPACE=~/odin-workspace
export GITHUB_TOKEN=ghp_xxxxx...
```

## Creating New Lessons

1. Choose appropriate category directory
2. Create `.md` file with descriptive name (kebab-case)
3. Add YAML frontmatter with metadata
4. Write lesson content in Markdown
5. Include code examples when applicable

### Lesson Template

```markdown
---
title: Your Lesson Title
category: patterns
tags: relevant, tags, here
applicable_to: Java, Spring
difficulty: intermediate
---

# Lesson Title

## Overview
Brief description of the concept.

## Key Points
- Point 1
- Point 2

## Detection in Code
How to identify this pattern/issue in code.

## Improvements to Suggest
What to recommend when this is found.

## Example
Code examples showing before/after.
```

## Best Practices for Lessons

1. **Be Specific** - Focus on one concept per lesson
2. **Use Examples** - Include real code examples
3. **Explain Why** - Not just what, but why it matters
4. **Prioritize** - Indicate importance of improvements
5. **Context** - Specify when to apply the lesson

## Future Enhancements

- [ ] RAG (Retrieval-Augmented Generation) using LLM
- [ ] Semantic search for relevant lessons
- [ ] Lesson versioning and updates
- [ ] Community contributions
- [ ] Lesson effectiveness metrics
- [ ] Auto-generate lessons from successful improvements

## Usage Examples

### Analyze with Brain Knowledge

```java
OdinAgent odin = new OdinAgent();
OdinBrain brain = new OdinBrain();
brain.initialize();

// Brain influences analysis
OdinAnalysisResult result = odin.analyze(projectPath);
```

### Clone and Improve External Project

```java
OdinBrain brain = new OdinBrain();

// Clone Valhalla-Co repository
Path repoPath = brain.cloneRepository("example-service");

// Apply Odin knowledge
Path improvedRepo = brain.applyKnowledgeToRepository("example-service");

// Repository is now in: workspace/branches/example-service
// With branch: odin/improvements-<timestamp>
```

### Manual Repository Operations

```java
RepositoryManager repoMgr = brain.getRepositoryManager();

// Create custom branch
repoMgr.createBranch(repoPath, "feature/odin-improvements");

// Commit changes
repoMgr.commit(repoPath, "Apply Odin improvements");

// Push to remote
repoMgr.push(repoPath, "feature/odin-improvements");
```

## Contributing Lessons

To contribute new lessons:

1. Fork the repository
2. Add your lesson in appropriate category
3. Follow the lesson format
4. Submit pull request
5. Lessons will be reviewed for quality and accuracy

---

**The more lessons Odin learns, the smarter it becomes at analyzing and improving code!**
