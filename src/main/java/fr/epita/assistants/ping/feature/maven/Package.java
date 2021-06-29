package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class Package implements Feature {
    public class ExecutionReportPackage implements Feature.ExecutionReport {
        public final boolean success;
        public String Output = "";


        public ExecutionReportPackage(Boolean success, String Output) {
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

        AnyProject p = (AnyProject) project;
        ProcessBuilder pb = new ProcessBuilder(p.config.mavenCmd, "package");

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            var process = pb.start();
            String result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new ExecutionReportPackage(true, result);

        } catch (Exception e) {
            return new ExecutionReportPackage(false, e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.PACKAGE;
    }
}
