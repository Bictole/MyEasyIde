package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class Stop implements Feature {

    public class ExecutionReportStop implements Feature.ExecutionReport {
        public final boolean success;
        public String output = "";

        public ExecutionReportStop(Boolean success, String errorMessage) {
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
        if (params.length != 1)
            return new Stop.ExecutionReportStop(false, "Too much arguments");

        Process ongoing = (Process) params[0];
        if (ongoing != null) {
            ongoing.destroy();
            return new ExecutionReportStop(true, "Exec Stoped.");
        }
        else
            return new ExecutionReportStop(true, "No Ongoing Process to stop.");
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.STOP;
    }
}
