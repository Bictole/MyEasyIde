package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class Install implements Feature {
    public class ExecutionReportInstall implements Feature.ExecutionReport {
        public final boolean success;
        public String Output = "";


        public ExecutionReportInstall(Boolean success, String Output) {
            this.success = success;
            this.Output = Output;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return Output;
        }
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Object... params) {

        ProcessBuilder pb = new ProcessBuilder("mvn", "install");

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            var process = pb.start();
            String result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new ExecutionReportInstall(true, result);

        } catch (Exception e) {
            return new ExecutionReportInstall(false, "Maven Exec failed :" + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.INSTALL;
    }
}
