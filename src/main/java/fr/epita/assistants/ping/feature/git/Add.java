package fr.epita.assistants.ping.feature.git;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Add implements Feature {

    private class ExecutionReportAdd implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportAdd() {
            this.success = true;
        }

        public ExecutionReportAdd(String errorMessage) {
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
            return new Add.ExecutionReportAdd("Execution need arguments to add -> jgit add error");

        try {
            return add((AnyProject) project, params);
        } catch (Exception e) {
            e.printStackTrace();
            return new Add.ExecutionReportAdd("Error when execution -> jgit add error");
        }
    }

    private ExecutionReportAdd add(AnyProject project, Object... params) throws IOException, GitAPIException {
        Git git = project.getgit();

        for (var toAdd : params) {
            git.add().addFilepattern((String) toAdd).call();
        }

        return new ExecutionReportAdd();
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.ADD;
    }
}
