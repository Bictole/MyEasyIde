package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Dist implements Feature {
    private class ExecutionReportDist implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportDist() {
            this.success = true;
        }
        public ExecutionReportDist(String errorMessage) {
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
            return new Dist.ExecutionReportDist("Too much argument provided");

        project.getFeature(Mandatory.Features.Any.CLEANUP);
        return dist();
    }

    private Dist.ExecutionReportDist dist() {
        try {
            Scanner scan = new Scanner(Paths.get(".myideignore"));
            while (scan.hasNext()) {
                String line = scan.nextLine().toString();
                delete(Path.of(line));
            }
        }
        catch (Exception e)
        {
            return new Dist.ExecutionReportDist("Deletion failed");
        }

        return new Dist.ExecutionReportDist();
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
        return Mandatory.Features.Any.DIST;
    }
}
