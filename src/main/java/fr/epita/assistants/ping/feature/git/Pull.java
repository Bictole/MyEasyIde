package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

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
        try {
            return pull(project);
        } catch (Exception e) {
            return new Pull.ExecutionReportPull(e.getMessage());
        }
    }

    private Pull.ExecutionReportPull pull(Project project) throws IOException, GitAPIException {
        PrintStream previous = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        Git git = Git.open(new File(String.valueOf(project.getRootNode().getPath())));

        var result = git.pull()
                .setRemote("origin")
                .setRemoteBranchName("master")
                .call();

        System.setOut(previous);
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
