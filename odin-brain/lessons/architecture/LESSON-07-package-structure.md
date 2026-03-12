---
title: Package Structure Pattern
category: architecture
tags: java, package, structure, organization
applicable_to: Java, Kotlin, Scala
difficulty: intermediate
---

# Package Structure Pattern

## Overview
Clean, logical package organization that reflects the application's purpose and makes code easy to navigate and understand.

## Key Principles

### Structure by Feature/Purpose
```
com.company.product/
├── core/          # Main business logic
├── domain/        # Domain models
├── service/       # Services
├── repository/    # Data access
├── controller/    # API/UI controllers
└── util/          # Utilities
```

### Odin Agent Example
```
br.com.valhalla.odin/
├── core/          # Main agent logic
├── brain/         # Learning system
├── git/           # Git operations
├── discovery/     # Project discovery
├── patterns/      # Pattern detection
└── specs/         # Improvement specs
```

## Detection in Code

Look for:
- Deep package nesting (>5 levels)
- Unclear package names
- Mixed responsibilities in one package
- "Util" or "Helper" as primary packages
- Packages with too many or too few classes

### Anti-Patterns
```java
❌ com.company.app.stuff
❌ com.company.app.misc
❌ com.company.app.helpers.utils.common
❌ com.company.app.module1.submodule.subsub.feature
```

### Good Patterns
```java
✅ com.company.app.user
✅ com.company.app.payment
✅ com.company.app.notification
✅ com.company.app.security
```

## Improvements to Suggest

### High Priority
1. **Flatten deep hierarchies**: Max 4-5 levels
2. **Rename vague packages**: "stuff", "misc", "helpers"
3. **Group by feature**: Not by layer
4. **Separate concerns**: One responsibility per package

### Medium Priority
1. **Extract submodules**: Large packages to modules
2. **Consistent naming**: Use domain language
3. **Document structure**: Add package-info.java
4. **Enforce boundaries**: Use package-private

### Low Priority
1. **Modularize**: Convert to Java modules
2. **Dependency rules**: Enforce with ArchUnit
3. **Package diagrams**: Visualize structure

## Example

### Before (Layer-Based)
```
com.company.app/
├── controllers/
│   ├── UserController.java
│   ├── OrderController.java
│   └── PaymentController.java
├── services/
│   ├── UserService.java
│   ├── OrderService.java
│   └── PaymentService.java
└── repositories/
    ├── UserRepository.java
    ├── OrderRepository.java
    └── PaymentRepository.java
```

### After (Feature-Based)
```
com.company.app/
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   └── User.java
├── order/
│   ├── OrderController.java
│   ├── OrderService.java
│   ├── OrderRepository.java
│   └── Order.java
└── payment/
    ├── PaymentController.java
    ├── PaymentService.java
    ├── PaymentRepository.java
    └── Payment.java
```

### Hybrid Approach (Large Projects)
```
com.company.app/
├── user/
│   ├── api/            # Controllers
│   ├── domain/         # Entities
│   ├── service/        # Business logic
│   └── persistence/    # Repositories
├── order/
│   ├── api/
│   ├── domain/
│   ├── service/
│   └── persistence/
└── shared/
    ├── security/
    ├── util/
    └── config/
```

## When to Apply

- **New projects**: Start with clean structure
- **Refactoring**: Improve existing organization
- **Team growth**: Clear boundaries help scaling
- **Microservices**: Each service needs clean structure

## Benefits

✅ Easy to navigate codebase  
✅ Clear responsibilities  
✅ Better encapsulation  
✅ Easier testing  
✅ Supports team scaling  
✅ Feature-based development  

## Anti-Patterns to Avoid

❌ "Manager", "Helper", "Util" packages everywhere  
❌ Mixing layers and features  
❌ Cyclic dependencies between packages  
❌ One giant package with everything  
❌ Deep nesting without reason  

## Package Naming Rules

### Do
- Use lowercase only
- Use singular nouns (user, not users)
- Use domain language
- Keep short and clear
- Avoid abbreviations

### Don't
- Use "Impl", "Base", "Abstract" in package names
- Use technical terms only (dto, dao, vo)
- Use version numbers in packages
- Use underscores or hyphens
- Mix languages (english + portuguese)

## Related Patterns

- Clean Architecture (hexagonal-architecture.md)
- Repository Pattern (repository-pattern.md)
- Domain-Driven Design (future lesson)

---

**Pattern Difficulty**: Intermediate  
**Impact**: High (code organization)  
**Effort to Apply**: High (requires refactoring)
