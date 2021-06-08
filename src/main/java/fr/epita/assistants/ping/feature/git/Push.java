package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;

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
        if (params.length <= 0)
            return new Push.ExecutionReportPush("Execution need arguments to add -> jgit add error");

        try {
            return pull(project);
        } catch (Exception e) {
            e.printStackTrace();
            return new Push.ExecutionReportPush("Error when execution -> jgit add error");
        }
    }

    private Push.ExecutionReportPush pull(Project project) throws IOException {
        Git git = Git.open(new File(String.valueOf(project.getRootNode().getPath())));

        git.push();
        return new Push.ExecutionReportPush();
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.PUSH;
    }
}
