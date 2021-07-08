package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.File;

public class Run implements Feature {

    public class ExecutionReportRun implements Feature.ExecutionReport {
        public final boolean success;
        public String output = "";

        public ExecutionReportRun(Boolean success, String errorMessage) {
            this.success = success;
            this.output = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }
    }


    @Override
    public ExecutionReport execute(Project project, Object... params) {
        String execFile = (String) params[0];
        String execParentPath = (String) params[1];
        String mainClass= (String) params[2];
        String packagePath = (String) params[3];
        var compiled = compile(project, execFile, execParentPath);
        if (!compiled.isSuccess())
            return compiled;

        return run(project, mainClass, packagePath);

    }

    private ExecutionReportRun compile(Project project, String main, String path)
    {
        ProcessBuilder pb = new ProcessBuilder("javac", main);
        String result = "";
        String error = "";

        try {
            pb.directory(new File(path));
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            error = new String(process.getErrorStream().readAllBytes());

            process.waitFor();
            if (!error.isEmpty())
                return new ExecutionReportRun(false, error);

            return new ExecutionReportRun(true, result);
        }
        catch (Exception e)
        {
            return new ExecutionReportRun(false, error + '\n' + e.getMessage());
        }

    }

    private ExecutionReport run(Project project, String main, String packagePath)
    {
        ProcessBuilder pb = new ProcessBuilder("java", main);
        String result = "";
        String error = "";

        try {
            pb.directory(new File(packagePath));
            Process process = pb.start();
            result = new String(process.getInputStream().readAllBytes());
            error = new String(process.getErrorStream().readAllBytes());

            process.waitFor();
            if (!error.isEmpty())
                return new ExecutionReportRun(false, error);

            return new ExecutionReportRun(true, result);
        }
        catch (Exception e)
        {
            return new ExecutionReportRun(false, error + '\n' + e.getMessage());
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.RUN;
    }
}
