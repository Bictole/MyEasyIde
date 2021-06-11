package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

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
        ProcessBuilder pb = new ProcessBuilder("mvn", "clean");

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            pb.start().waitFor();

            return new ExecutionReportClean();

        } catch (Exception e) {

            return new ExecutionReportClean("Maven Clean failed :" + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.CLEAN;
    }
}
