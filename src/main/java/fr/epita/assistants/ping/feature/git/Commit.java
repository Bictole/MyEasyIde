package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

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
        try {
            var msg = "";
            if (params.length > 0)
                msg = (String) params[0];
            return commit((AnyProject) project, msg);
        } catch (Exception e) {
            return new Commit.ExecutionReportCommit(e.getMessage());
        }
    }

    private Commit.ExecutionReportCommit commit(AnyProject project, Object msg) throws IOException, GitAPIException {
        Git git = project.getgit();

        git.commit().setMessage((String) msg).call();

        return new Commit.ExecutionReportCommit();
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.COMMIT;
    }
}
