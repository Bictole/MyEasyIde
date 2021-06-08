package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Project;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

import java.util.Arrays;

public class Clean implements Feature {
    @Override
    public Feature.ExecutionReport execute(Project project, Object... params) {
        Maven mvn = new DefaultMaven();
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setBaseDirectory(project.getRootNode().getPath().toFile());
        request.setGoals(Arrays.asList("clean"));
        mvn.execute(request);
        return null;
    }

    @Override
    public Type type() {
        return null;
    }
}
