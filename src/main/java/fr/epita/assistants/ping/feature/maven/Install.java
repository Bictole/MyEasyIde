package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.util.Arrays;

public class Install implements Feature {
    private class ExecutionReportInstall implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportInstall() {
            this.success = true;
        }

        public ExecutionReportInstall(String errorMessage) {
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
    public Feature.ExecutionReport execute(Project project, Object... params) {

        try {
            Maven mvn = new DefaultMaven();
            MavenExecutionRequest request = new DefaultMavenExecutionRequest();
            request.setBaseDirectory(project.getRootNode().getPath().toFile());
            request.setGoals(Arrays.asList("install"));
            mvn.execute(request);
            return new Install.ExecutionReportInstall();
        }
        catch (Exception e)
        {
            return new Install.ExecutionReportInstall("Maven Install failed :" + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.INSTALL;
    }
}
