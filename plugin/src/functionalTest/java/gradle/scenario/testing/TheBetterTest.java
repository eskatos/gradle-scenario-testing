package gradle.scenario.testing;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheBetterTest {

    @Test
    public void theBetterTest() {
        writeString(getSettingsFile(), "rootProject.name = 'the-test'");
        writeString(getBuildFile(), "plugins { id 'gradle.scenario.testing.greeting' }");

        System.out.println(underTestBuildDirectory);

        String taskPath = ":theTask";
        Consumer<File> initialUnderTestBuildDirectoryAssertion =
                dir -> assertEquals("Coucou", readStringFrom(new File(dir, "build/the-file.txt")));
        Consumer<File> underTestBuildDirectoryMutation =
                dir -> writeString(new File(dir, "gradle.properties"), "theText=Hi");
        Consumer<File> mutatedUnderTestBuildDirectoryAssertion =
                dir -> assertEquals("Hi", readStringFrom(new File(dir, "build/the-file.txt")));

        new TheFixture().runUpToDateCheckingScenario(
                underTestBuildDirectory,
                taskPath,
                initialUnderTestBuildDirectoryAssertion,
                underTestBuildDirectoryMutation,
                mutatedUnderTestBuildDirectoryAssertion
        );
    }

    @TempDir
    File underTestBuildDirectory;

    private File getBuildFile() {
        return new File(underTestBuildDirectory, "build.gradle");
    }

    private File getSettingsFile() {
        return new File(underTestBuildDirectory, "settings.gradle");
    }

    private void writeString(File file, String string) {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private String readStringFrom(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
