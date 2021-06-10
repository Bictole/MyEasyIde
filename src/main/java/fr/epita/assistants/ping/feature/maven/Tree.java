package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.util.Arrays;

public class Tree implements Feature {
    private class ExecutionReportTree implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportTree() {
            this.success = true;
        }

        public ExecutionReportTree(String errorMessage) {
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
            DefaultMaven mvn = new DefaultMaven();
            DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();
            request.setBaseDirectory(project.getRootNode().getPath().toFile());
            request.setGoals(Arrays.asList("tree"));
            mvn.execute(request);
            return new Tree.ExecutionReportTree();
        }
        catch (Exception e)
        {
            return new Tree.ExecutionReportTree("Maven Tree failed :" + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.TREE;
    }
}
