package br.com.valhalla.core.brain;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Repositório de lessons.
 * Carrega e indexa todas as lessons disponíveis.
 */
public class LessonRepository implements KnowledgeBase {

    private final Path brainPath;
    private final LessonParser parser;
    private final Map<String, Lesson> lessonsById;
    private final Map<String, List<Lesson>> lessonsByCategory;

    public LessonRepository(Path brainPath) {
        this.brainPath = brainPath;
        this.parser = new LessonParser();
        this.lessonsById = new HashMap<>();
        this.lessonsByCategory = new HashMap<>();
    }

    /**
     * Carrega todas as lessons do diretório brain
     */
    public void load() throws IOException {
        Path lessonsPath = brainPath.resolve("lessons");

        if (!Files.exists(lessonsPath)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(lessonsPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".md"))
                 .forEach(this::loadLesson);
        }
    }

    /**
     * Carrega uma lesson individual
     */
    private void loadLesson(Path lessonFile) {
        try {
            Lesson lesson = parser.parse(lessonFile);
            lessonsById.put(lesson.getId(), lesson);

            lessonsByCategory
                .computeIfAbsent(lesson.getCategory(), k -> new ArrayList<>())
                .add(lesson);

        } catch (IOException e) {
            System.err.println("Failed to load lesson: " + lessonFile + " - " + e.getMessage());
        }
    }

    /**
     * Busca lessons relevantes para um contexto
     */
    public List<Lesson> findRelevant(String projectType, String language, List<String> tags) {
        return lessonsById.values().stream()
            .filter(lesson -> lesson.isRelevantFor(projectType, language))
            .filter(lesson -> hasCommonTags(lesson, tags))
            .collect(Collectors.toList());
    }

    /**
     * Busca lessons por categoria
     */
    public List<Lesson> findByCategory(String category) {
        return lessonsByCategory.getOrDefault(category, List.of());
    }

    /**
     * Busca lesson por ID
     */
    public Optional<Lesson> findById(String id) {
        return Optional.ofNullable(lessonsById.get(id));
    }

    /**
     * Retorna todas as lessons carregadas
     */
    public List<Lesson> getAll() {
        return new ArrayList<>(lessonsById.values());
    }

    /**
     * Verifica se há tags em comum
     */
    private boolean hasCommonTags(Lesson lesson, List<String> searchTags) {
        if (searchTags == null || searchTags.isEmpty()) {
            return true;
        }

        return lesson.getTags().stream()
                    .anyMatch(tag -> searchTags.contains(tag));
    }

    public int size() {
        return lessonsById.size();
    }

    @Override
    public void initialize() {
        try {
            load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize knowledge base", e);
        }
    }

    @Override
    public List<Lesson> findByTag(String tag) {
        return lessonsById.values().stream()
                .filter(lesson -> lesson.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    @Override
    public List<Lesson> getAllLessons() {
        return getAll();
    }

    @Override
    public Path getBrainPath() {
        return brainPath;
    }
}
