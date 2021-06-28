package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.feature.maven.Compile;

import java.io.File;
import java.util.Optional;

public class Run implements Feature {

    public class ExecutionReportRun implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportRun() {

            this.success = true;
        }
        public ExecutionReportRun(String errorMessage) {
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
    public ExecutionReport execute(Project project, Object... params) {
        Optional<String> compiled = compile(project, "src.main.java.Main");
        if (compiled.isEmpty())
            return new ExecutionReportRun("Compilation Failed");

        return run(project, compiled.get());

    }

    private Optional<String> compile(Project project, String main)
    {
        ProcessBuilder pb = new ProcessBuilder("javac", "Main.java");

        try {
            pb.directory(new File(project.getRootNode().getPath() + "/src/main/java/"));
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.start().waitFor();

            return Optional.of("Main");
        }
        catch (Exception e)
        {
            return Optional.empty();
        }

    }

    private ExecutionReport run(Project project, String main)
    {
        ProcessBuilder pb = new ProcessBuilder("java", "Main");

        try {
            pb.directory(new File(project.getRootNode().getPath() + "/src/main/java/"));
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.start().waitFor();

            return new ExecutionReportRun();
        }
        catch (Exception e)
        {
            return new ExecutionReportRun(e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.RUN;
    }
}
