package fr.epita.assistants.ping.feature.git;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Commit implements Feature {
    private class ExecutionReportCommit implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportCommit() {
            this.success = true;
        }

        public ExecutionReportCommit(String errorMessage) {
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
            return new Commit.ExecutionReportCommit("Execution need arguments to add -> jgit commit error");

        try {
            return commit(project, params[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Commit.ExecutionReportCommit("Error when execution -> jgit commit error");
        }
    }

    private Commit.ExecutionReportCommit commit(Project project, Object msg) throws IOException, GitAPIException {
        Git git = Git.open(new File(String.valueOf(project.getRootNode().getPath())));

        git.commit().setMessage((String) msg).call();

        return new Commit.ExecutionReportCommit();
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.COMMIT;
    }
}