package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Exec implements Feature {

    public class ExecutionReportExecute implements Feature.ExecutionReport {
        public final boolean success;
        public String Output = "";


        public ExecutionReportExecute(Boolean success, String Output) {
            this.success = success;
            this.Output = Output;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return Output;
        }
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Object... params) {

        ProcessBuilder pb = new ProcessBuilder("mvn", "exec");

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            var process = pb.start();
            String result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new Exec.ExecutionReportExecute(true, result);

        } catch (Exception e) {
            return new Exec.ExecutionReportExecute(false, "Maven Exec failed :" + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.EXEC;
    }
}
