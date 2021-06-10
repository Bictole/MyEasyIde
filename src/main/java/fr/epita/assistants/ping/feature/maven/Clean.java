package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Clean implements Feature {

    static class ExecutionReportClean implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportClean() {
            this.success = true;
        }

        public ExecutionReportClean(String errorMessage) {
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
            request.setGoals(Arrays.asList("clean"));
            mvn.execute(request);
            return new ExecutionReportClean();
        }
        catch (Exception e)
        {
            return new ExecutionReportClean("Maven Clean failed :" + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.CLEAN;
    }
}
