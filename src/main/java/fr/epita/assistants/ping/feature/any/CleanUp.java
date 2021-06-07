package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CleanUp implements Feature{
    private class ExecutionReportCleanUp implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportCleanUp() {
            this.success = true;
        }
        public ExecutionReportCleanUp(String errorMessage) {
            this.success = false;
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    @Override
    public ExecutionReport execute(Project project, Object... param) {
        if (param.length > 0)
            return new CleanUp.ExecutionReportCleanUp("Too much argument provided");
        return cleanUp();
    }

    private ExecutionReportCleanUp cleanUp() {
        try {
            Scanner scan = new Scanner(Paths.get(".myideignore"));
            while (scan.hasNext()) {
                String line = scan.nextLine().toString();
                delete(Path.of(line));
            }
        }
        catch (Exception e)
        {
            return new ExecutionReportCleanUp("Deletion failed");
        }

        return new ExecutionReportCleanUp();
    }
    
    private void delete(Path file) throws IOException
    {
        if (file.toFile().isDirectory())
        {
            for (var sub : file.toFile().listFiles()) {
                delete(sub.toPath());
            }
        }
        Files.deleteIfExists(file);
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.CLEANUP;
    }
}
