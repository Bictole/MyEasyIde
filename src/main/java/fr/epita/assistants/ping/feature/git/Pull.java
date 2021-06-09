package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Pull implements Feature {

    private class ExecutionReportPull implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportPull() {
            this.success = true;
        }

        public ExecutionReportPull(String errorMessage) {
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
            return new Pull.ExecutionReportPull("Execution need arguments to add -> jgit add error");

        try {
            return pull(project);
        } catch (Exception e) {
            e.printStackTrace();
            return new Pull.ExecutionReportPull("Error when execution -> jgit add error");
        }
    }

    private Pull.ExecutionReportPull pull(Project project) throws IOException, GitAPIException {
        Git git = Git.open(new File(String.valueOf(project.getRootNode().getPath())));

        var result = git.pull()
                .setRemote("origin")
                .setRemoteBranchName("master")
                .call();

        if (result.isSuccessful()) {
            return new Pull.ExecutionReportPull();
        } else {
            return new Pull.ExecutionReportPull("Pull failed : " + result.toString());
        }

    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.PULL;
    }
}
