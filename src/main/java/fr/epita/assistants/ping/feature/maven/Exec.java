package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.project.AnyProject;

import java.util.ArrayList;
import java.util.List;

public class Exec implements Feature {

    public class ExecutionReportExecute implements Feature.ExecutionReport {
        public final boolean success;
        public String Output = "";


        public ExecutionReportExecute(Boolean success, String Output) {
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
        String mainClass = (String) params[0];
        String args = (String) params[1];
        AnyProject p = (AnyProject) project;
        List<String> execCmd = new ArrayList<>();
        execCmd.add(p.config.mavenCmd);
        execCmd.add( "compile");
        execCmd.add("exec:java");
        execCmd.add("-Dexec.mainClass=" + mainClass);
        List<String> split_args = List.of(args.split(" "));
        split_args = split_args.stream().filter(a -> !a.equals("")).toList();
        args = "";
        if (!split_args.isEmpty())
        {
            for (int i = 0; i < split_args.size() - 1; i++){
                args += split_args.get(i) + " ";
            }
            args += split_args.get(split_args.size() - 1);
        }
        execCmd.add("-Dexec.args=" + args);
        ProcessBuilder pb = new ProcessBuilder( execCmd);
        String result = "";

        try {
            pb.directory(project.getRootNode().getPath().toFile());
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return new Exec.ExecutionReportExecute(true, result);
        }
        catch (Exception e)
        {
            return new Exec.ExecutionReportExecute(false, result + '\n' + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.EXEC;
    }
}
