package test.service;

import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collections;

public class ProjectServiceTest {

    private ProjectManager projectManager;

    @Test
    public void testEmptyFolder()
    {
        projectManager = new ProjectManager();
        Project project = projectManager.load(Paths.get("empty_folder"));
        assert(project.getRootNode().getChildren().equals(Collections.emptyList()));
    }
}
