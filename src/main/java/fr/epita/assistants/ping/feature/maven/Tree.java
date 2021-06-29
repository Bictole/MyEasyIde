package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;

public class Tree implements Feature {

    public class ExecutionReportTree implements Feature.ExecutionReport {
        public final boolean success;
        public String output = "";


        public ExecutionReportTree(Boolean success, String Output) {
            this.success = success;
            this.output = Output;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getOutput() { return output; }
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Object... params) {
        AnyProject p = (AnyProject) project;
        ProcessBuilder pb = new ProcessBuilder(p.config.mavenCmd, "dependency:tree", (String) params[0]);
        String result = "";

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new Tree.ExecutionReportTree(true, result);
        }
        catch (Exception e)
        {
            return new Tree.ExecutionReportTree(false, e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.TREE;
    }
}
