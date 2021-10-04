package gradle.scenario.testing;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheTest {

    @TempDir
    File underTestBuildDirectory;

    private File getBuildFile() {
        return new File(underTestBuildDirectory, "build.gradle");
    }

    private File getSettingsFile() {
        return new File(underTestBuildDirectory, "settings.gradle");
    }


    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }

    @Test
    public void theTest() throws IOException {
        writeString(getSettingsFile(), "rootProject.name = 'the-test'");
        writeString(getBuildFile(), "plugins { id 'gradle.scenario.testing.greeting' }");

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("theTask");
        runner.withProjectDir(underTestBuildDirectory);
        BuildResult result = runner.build();

        // Verify the result
        assertEquals(result.task(":theTask").getOutcome(), TaskOutcome.SUCCESS);
        assertEquals("Coucou", readStringFrom(new File(underTestBuildDirectory, "build/the-file.txt")));

        result = runner.build();
        assertEquals(result.task(":theTask").getOutcome(), TaskOutcome.UP_TO_DATE);
        assertEquals("Coucou", readStringFrom(new File(underTestBuildDirectory, "build/the-file.txt")));

        result = runner.withArguments("theTask", "-PtheText=Hi").build();
        assertEquals(result.task(":theTask").getOutcome(), TaskOutcome.SUCCESS);
        assertEquals("Hi", readStringFrom(new File(underTestBuildDirectory, "build/the-file.txt")));
    }

    private String readStringFrom(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
