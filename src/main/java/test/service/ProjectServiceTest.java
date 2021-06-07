package test.service;

import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.Test;

import java.nio.file.Paths;

public class ProjectServiceTest {

    private ProjectManager projectManager;

    @Test
    public void testEmptyFolder()
    {
        projectManager = new ProjectManager();
        projectManager.load(Paths.get("empty_folder"));
    }
}
