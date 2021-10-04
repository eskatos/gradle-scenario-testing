package gradle.scenario.testing;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;

import java.io.File;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheFixture {


    public void runUpToDateCheckingScenario(
            File underTestBuildDirectory,
            String taskPath,
            Consumer<File> initialUnderTestBuildDirectoryAssertion,
            Consumer<File> underTestBuildDirectoryMutation,
            Consumer<File> mutatedUnderTestBuildDirectoryAssertion
    ) {

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments(taskPath);
        runner.withProjectDir(underTestBuildDirectory);
        BuildResult result = runner.build();

        // First run
        assertEquals(result.task(taskPath).getOutcome(), TaskOutcome.SUCCESS);
        initialUnderTestBuildDirectoryAssertion.accept(underTestBuildDirectory);

        result = runner.build();
        assertEquals(result.task(taskPath).getOutcome(), TaskOutcome.UP_TO_DATE);
        initialUnderTestBuildDirectoryAssertion.accept(underTestBuildDirectory);

        underTestBuildDirectoryMutation.accept(underTestBuildDirectory);

        result = runner.build();
        assertEquals(result.task(taskPath).getOutcome(), TaskOutcome.SUCCESS);
        mutatedUnderTestBuildDirectoryAssertion.accept(underTestBuildDirectory);
    }
}
