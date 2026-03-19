package br.com.valhalla.core.brain;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LessonParserTest {

    @Test
    public void parseFrontMatterAndContent() throws Exception {
        String md = "---\n" +
                "title: Test Lesson\n" +
                "tags: java,test\n" +
                "---\n\n" +
                "# Heading\n\nContent here.";

        Path tmp = Files.createTempFile("lesson-test", ".md");
        Files.writeString(tmp, md);

        LessonParser parser = new LessonParser();
        Lesson lesson = parser.parse(tmp);

        // createTempFile appends a random suffix; assert startsWith instead of exact equality
        assertTrue(lesson.getId().startsWith("lesson-test"));
        assertEquals("Test Lesson", lesson.getTitle());
        assertTrue(lesson.getTags().contains("java"));
        assertTrue(lesson.getContent().contains("Content here."));

        Files.deleteIfExists(tmp);
    }
}
