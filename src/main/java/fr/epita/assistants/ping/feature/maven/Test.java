package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.util.Arrays;

public class Test implements Feature {

    public class ExecutionReportTest implements Feature.ExecutionReport {
        public final boolean success;
        public String output;

        public ExecutionReportTest(Boolean success, String output) {
            this.success = true;
            this.output = output;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Object... params) {
        AnyProject p = (AnyProject) project;
        ProcessBuilder pb = new ProcessBuilder(p.config.mavenCmd, "test");
        String result = "";

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new Test.ExecutionReportTest(true, result);
        } catch (Exception e) {
            return new Test.ExecutionReportTest(false, e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.TEST;
    }
}
