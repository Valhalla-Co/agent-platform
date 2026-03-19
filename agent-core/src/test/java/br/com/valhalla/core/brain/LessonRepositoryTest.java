package br.com.valhalla.core.brain;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LessonRepositoryTest {

    @Test
    public void loadLessonsFromDirectory() throws Exception {
        Path tmpDir = Files.createTempDirectory("brain-test");
        Path lessonsDir = tmpDir.resolve("lessons");
        Files.createDirectories(lessonsDir);

        String md = "---\n" +
                "title: Repo Lesson\n" +
                "tags: repo,test\n" +
                "---\n\n" +
                "Content for repo lesson.";

        Path lessonFile = lessonsDir.resolve("repo-lesson.md");
        Files.writeString(lessonFile, md);

        LessonRepository repo = new LessonRepository(tmpDir);
        repo.initialize();

        assertEquals(1, repo.size());
        assertTrue(repo.findByTag("repo").size() == 1);

        // cleanup
        Files.deleteIfExists(lessonFile);
        Files.deleteIfExists(lessonsDir);
        Files.deleteIfExists(tmpDir);
    }
}
