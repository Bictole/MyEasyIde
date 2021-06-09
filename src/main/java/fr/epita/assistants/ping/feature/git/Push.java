package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Push implements Feature {
    private class ExecutionReportPush implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportPush() {
            this.success = true;
        }

        public ExecutionReportPush(String errorMessage) {
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
        try {
            return pull((AnyProject) project);
        } catch (Exception e) {
            e.printStackTrace();
            return new Push.ExecutionReportPush("Error when execution -> jgit add error");
        }
    }

    private Push.ExecutionReportPush pull(AnyProject project) throws IOException, GitAPIException {
        Git git = project.getgit();
        try {
            git.push().setRemote("origin").add("master").call();
            return new Push.ExecutionReportPush();
        }
        catch (Exception e)
        {
            return new Push.ExecutionReportPush("Push failed : "+ e.getMessage());
        }
    }


    @Override
    public Type type() {
        return Mandatory.Features.Git.PUSH;
    }
}
