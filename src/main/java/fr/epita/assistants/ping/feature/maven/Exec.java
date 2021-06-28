package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.feature.any.Run;
import fr.epita.assistants.ping.project.AnyProject;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.io.File;
import java.io.OutputStream;
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

        String mainClass = (String) params[0];
        AnyProject p = (AnyProject) project;
        ProcessBuilder pb = new ProcessBuilder(p.config.mavenCmd, "compile",
                "exec:java", "-Dexec.mainClass=" + mainClass);
        String result = "";

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new Exec.ExecutionReportExecute(true, result);
        }
        catch (Exception e)
        {
            return new Exec.ExecutionReportExecute(false, result + '\n' + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.EXEC;
    }
}
