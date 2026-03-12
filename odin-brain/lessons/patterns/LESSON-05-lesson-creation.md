---
title: Lesson Creation Pattern
category: patterns
tags: odin, brain, lessons, knowledge, documentation
applicable_to: Odin Agent, Documentation Systems
difficulty: intermediate
---

# Lesson Creation Pattern

## Overview
Standard format for creating lessons that Odin Agent can parse and learn from, enabling knowledge-based code analysis and improvements.

## Key Principles

### Lesson Structure
Every lesson must have:
1. **YAML Frontmatter**: Metadata for categorization
2. **Clear Title**: Descriptive and specific
3. **Overview**: Brief explanation
4. **Key Concepts**: Main points
5. **Detection**: How to identify in code
6. **Improvements**: What to suggest
7. **Examples**: Code samples

### Naming Convention
```
LESSON-##-descriptive-kebab-case-name.md

Examples:
LESSON-01-pr-body-management.md
LESSON-02-conventional-commits.md
LESSON-05-lesson-creation.md
```

## Detection in Code

Look for:
- Missing or incomplete lessons
- Lessons without proper frontmatter
- No examples or detection criteria
- Vague or unclear improvement suggestions
- Lessons in wrong categories

## Improvements to Suggest

### High Priority
1. **Add missing frontmatter**: Ensure all metadata present
2. **Include examples**: Every lesson needs code examples
3. **Define detection criteria**: How to spot the pattern
4. **Specify improvements**: Clear, actionable suggestions

### Medium Priority
1. **Add difficulty level**: Help users understand complexity
2. **Link related lessons**: Create knowledge graph
3. **Include anti-patterns**: Show what NOT to do
4. **Add benefits section**: Explain value

### Low Priority
1. **Add images/diagrams**: Visual aids for complex patterns
2. **Include metrics**: Success criteria
3. **Add references**: Links to external resources

## Example

### Complete Lesson Template
```markdown
---
title: Pattern Name
category: architecture|patterns|best-practices
tags: tag1, tag2, tag3
applicable_to: Java, Python, etc
difficulty: beginner|intermediate|advanced
---

# Pattern Name

## Overview
Brief 2-3 sentence description of the pattern.

## Key Principles

### Main Concepts
1. First principle
2. Second principle
3. Third principle

### Rules
- Rule 1
- Rule 2

## Detection in Code

Look for:
- Indicator 1
- Indicator 2
- Code smell 3

### Example Detection
\`\`\`java
// Bad example that violates pattern
public class Bad {
    // problematic code
}
\`\`\`

## Improvements to Suggest

### High Priority
1. **Critical improvement**: Description
2. **Important fix**: Description

### Medium Priority
1. **Good enhancement**: Description
2. **Nice improvement**: Description

### Low Priority
1. **Optional refinement**: Description

## Example

### Before (Anti-Pattern)
\`\`\`java
// Code showing the problem
\`\`\`

### After (Correct Pattern)
\`\`\`java
// Code showing the solution
\`\`\`

### Explanation
Why the change improves the code.

## When to Apply

- **Situation 1**: When X happens
- **Situation 2**: When Y is true
- **Situation 3**: Always in Z context

## Benefits

✅ Benefit 1
✅ Benefit 2
✅ Benefit 3

## Anti-Patterns to Avoid

❌ Anti-pattern 1
❌ Anti-pattern 2
❌ Anti-pattern 3

## Related Patterns

- Related Lesson 1 (LESSON-##)
- Related Lesson 2 (LESSON-##)
- External Pattern

---

**Pattern Difficulty**: beginner|intermediate|advanced
**Impact**: High|Medium|Low (what aspect)
**Effort to Apply**: Low|Medium|High (time estimate)
\`\`\`

### Frontmatter Fields

#### Required
- `title`: Clear, descriptive name
- `category`: architecture|patterns|best-practices
- `tags`: Comma-separated keywords
- `applicable_to`: Languages/frameworks
- `difficulty`: beginner|intermediate|advanced

#### Optional
- `author`: Lesson creator
- `version`: Lesson version
- `date`: Creation date
- `references`: External links

## When to Apply

- **New pattern identified**: Create lesson immediately
- **Best practice discovered**: Document as lesson
- **Common mistake**: Create anti-pattern lesson
- **Architecture decision**: Document reasoning

## Implementation Steps

### 1. Choose Category
```
odin-brain/lessons/
├── architecture/    # System design patterns
├── patterns/        # Design patterns
└── best-practices/  # Coding standards
```

### 2. Number the Lesson
Find the next available number:
```bash
ls odin-brain/lessons/**/*.md | grep LESSON- | sort
# Next number: LESSON-14
```

### 3. Create File
```bash
touch odin-brain/lessons/best-practices/LESSON-14-my-pattern.md
```

### 4. Add Content
Use the template above, fill all sections.

### 5. Validate
```bash
# Check YAML syntax
head -10 LESSON-14-my-pattern.md

# Verify Odin can parse
java -jar odin-agent.jar --validate-lessons
```

## Benefits

✅ Odin learns new patterns automatically  
✅ Consistent knowledge base  
✅ Easy to search and filter  
✅ Version controlled knowledge  
✅ Community can contribute  
✅ Measurable pattern detection  

## Anti-Patterns to Avoid

❌ Lessons without examples  
❌ Vague detection criteria  
❌ No frontmatter metadata  
❌ Overly complex lessons (split them)  
❌ Duplicate lessons  
❌ Lessons without clear improvement suggestions  

## Odin Integration

### How Odin Uses Lessons

1. **Load**: `OdinBrain.initialize()` loads all lessons
2. **Filter**: By category, tags, applicable_to
3. **Apply**: During project analysis
4. **Learn**: Extract detection patterns
5. **Suggest**: Generate improvements based on lessons

### Example Usage
```java
OdinBrain brain = new OdinBrain();
brain.initialize();

// Get lessons for Java projects
List<Lesson> javaLessons = brain.getRepository()
    .findRelevant("JAVA_MAVEN", "Java", List.of());

// Apply during analysis
ProjectContext context = discovery.discover(projectPath);
List<String> insights = brain.learn(context);
```

## Related Patterns

- Documentation Structure (LESSON-03)
- File Naming Conventions (LESSON-04)
- Knowledge Management (future lesson)

---

**Pattern Difficulty**: Intermediate  
**Impact**: High (Odin's intelligence)  
**Effort to Apply**: Medium (30-60 minutes per lesson)
