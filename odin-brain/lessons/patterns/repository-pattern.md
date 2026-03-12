---
title: Repository Pattern
category: patterns
tags: design-pattern, data-access, persistence
applicable_to: Java, C#, Enterprise
difficulty: intermediate
---

# Repository Pattern

## Overview
The Repository pattern mediates between the domain and data mapping layers, acting like an in-memory collection of domain objects.

## Purpose
- Decouple business logic from data access
- Centralize data access logic
- Make code more testable
- Provide a collection-like interface for domain objects

## Structure

```java
// Domain Entity
public class User {
    private UserId id;
    private String name;
    private Email email;
}

// Repository Interface (in domain)
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    List<User> findAll();
    void delete(UserId id);
}

// Repository Implementation (in infrastructure)
@Repository
public class JpaUserRepository implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }
}
```

## Detection in Code

Look for:
- Classes/interfaces ending with "Repository"
- Methods like: `save()`, `findById()`, `findAll()`, `delete()`
- Data access logic centralized in specific classes

## Anti-Patterns to Detect

1. **Generic Repository** - One repository for all entities
2. **Leaky Abstraction** - Exposing query details (SQL, JPA Criteria)
3. **Fat Repository** - Business logic in repository

## Improvements to Suggest

### High Priority
- Extract data access from services to repositories
- Create repository interfaces in domain layer
- Implement repositories in infrastructure layer

### Medium Priority
- Use specific repositories per aggregate
- Add query methods that express business intent
- Avoid exposing implementation details

### Low Priority
- Consider specification pattern for complex queries
- Add caching layer if needed

## Example Improvement

**Before:**
```java
@Service
public class UserService {
    @Autowired
    private EntityManager em;
    
    public User getUser(Long id) {
        return em.find(User.class, id);  // Data access in service
    }
}
```

**After:**
```java
// Domain
public interface UserRepository {
    Optional<User> findById(UserId id);
}

// Infrastructure
@Repository
public class JpaUserRepository implements UserRepository {
    @PersistenceContext
    private EntityManager em;
    
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(em.find(UserEntity.class, id.getValue()))
                       .map(this::toDomain);
    }
}

// Service
@Service
public class UserService {
    private final UserRepository repository;
    
    public User getUser(UserId id) {
        return repository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

## When to Apply

- Projects with data persistence
- Systems using ORM (Hibernate, JPA, Entity Framework)
- Applications following DDD or Clean Architecture
- Code with scattered data access logic
