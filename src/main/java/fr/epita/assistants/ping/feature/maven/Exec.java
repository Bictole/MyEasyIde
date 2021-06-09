package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class Exec implements Feature {
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return null;
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.EXEC;
    }
}
