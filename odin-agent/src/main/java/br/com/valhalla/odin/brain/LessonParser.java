package br.com.valhalla.odin.brain;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parser de Markdown para lessons.
 * Extrai metadados e conteúdo de arquivos MD.
 */
public class LessonParser {

    private static final Pattern METADATA_PATTERN = Pattern.compile("^---\\s*$(.+?)^---\\s*$",
                                                                    Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern METADATA_LINE = Pattern.compile("^([\\w_]+):\\s*(.+)$");

    /**
     * Faz parse de um arquivo MD de lesson
     */
    public Lesson parse(Path lessonFile) throws IOException {
        String content = Files.readString(lessonFile);

        Map<String, String> metadata = extractMetadata(content);
        String mainContent = removeMetadata(content);

        String id = lessonFile.getFileName().toString().replace(".md", "");
        String title = metadata.getOrDefault("title", id);
        String category = detectCategory(lessonFile);
        List<String> tags = parseTags(metadata.get("tags"));

        return new Lesson(id, title, category, mainContent, tags, metadata);
    }

    /**
     * Extrai metadados YAML frontmatter
     */
    private Map<String, String> extractMetadata(String content) {
        Map<String, String> metadata = new HashMap<>();

        Matcher matcher = METADATA_PATTERN.matcher(content);
        if (matcher.find()) {
            String metadataBlock = matcher.group(1);
            String[] lines = metadataBlock.split("\\n");

            for (String line : lines) {
                Matcher lineMatcher = METADATA_LINE.matcher(line.trim());
                if (lineMatcher.matches()) {
                    String key = lineMatcher.group(1);
                    String value = lineMatcher.group(2).trim();
                    metadata.put(key, value);
                }
            }
        }

        return metadata;
    }

    /**
     * Remove frontmatter metadata do conteúdo
     */
    private String removeMetadata(String content) {
        return METADATA_PATTERN.matcher(content).replaceFirst("").trim();
    }

    /**
     * Detecta categoria baseado no caminho do arquivo
     */
    private String detectCategory(Path lessonFile) {
        Path parent = lessonFile.getParent();
        if (parent != null) {
            return parent.getFileName().toString();
        }
        return "general";
    }

    /**
     * Parse tags separadas por vírgula
     */
    private List<String> parseTags(String tagsString) {
        if (tagsString == null || tagsString.isBlank()) {
            return List.of();
        }

        return Arrays.stream(tagsString.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .collect(Collectors.toList());
    }
}
