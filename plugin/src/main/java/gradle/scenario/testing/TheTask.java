package gradle.scenario.testing;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@CacheableTask
public abstract class TheTask extends DefaultTask {

    @Input
    abstract Property<String> getTheText();

    @OutputFile
    abstract RegularFileProperty getTheFile();

    @TaskAction
    public void action() throws IOException {
        File theFile = getTheFile().get().getAsFile();
        String theText = getTheText().get();
        Files.writeString(theFile.toPath(), theText);
    }
}
