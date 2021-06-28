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

public class Install implements Feature {
    public class ExecutionReportInstall implements Feature.ExecutionReport {
        public final boolean success;
        public String Output = "";


        public ExecutionReportInstall(Boolean success, String Output) {
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

        AnyProject p = (AnyProject) project;
        ProcessBuilder pb = new ProcessBuilder(p.config.mavenCmd, "install");

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            var process = pb.start();
            String result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new ExecutionReportInstall(true, result);

        } catch (Exception e) {
            return new ExecutionReportInstall(false, e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.INSTALL;
    }
}
