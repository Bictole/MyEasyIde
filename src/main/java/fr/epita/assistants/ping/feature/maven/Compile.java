package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Compile implements Feature {
    public class ExecutionReportCompile implements Feature.ExecutionReport {
        public final boolean success;
        public String output = "";

        public ExecutionReportCompile(Boolean success, String Output)
        {
            this.output = Output;
            this.success = success;
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
        ProcessBuilder pb = new ProcessBuilder(p.config.mavenCmd, "compile", "-DbuildDirectory=" + project.getRootNode().getPath().resolve("target"));

        String result = "";

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            pb.start().waitFor();
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            return new Compile.ExecutionReportCompile(true, result);
        }
        catch (Exception e)
        {
            return new Compile.ExecutionReportCompile(false, result + e.getMessage());
        }


    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.COMPILE;
    }
}
