package fr.epita.assistants.ping.feature.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Project;

public class Package implements Feature {
    @Override
    public Feature.ExecutionReport execute(Project project, Object... params) {
        return null;
    }

    @Override
    public Type type() {
        return null;
    }
}
