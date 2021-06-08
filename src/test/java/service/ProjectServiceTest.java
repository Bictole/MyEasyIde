package service;

import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectServiceTest {

    private ProjectManager projectManager;

    @BeforeEach
    public void setUp(){
        projectManager = new ProjectManager();
    }

    @Test
    public void EmptyFolderTest()
    {
        Project project = projectManager.load(Path.of("ProjectTests/EmptyFolder"));
        assertEquals(Paths.get("ProjectTests/EmptyFolder"), project.getRootNode().getPath());
        assertEquals(Collections.emptyList(), project.getRootNode().getChildren());
    }

    @Test
    public void OneFileDirectoryTest()
    {
        Project project = projectManager.load(Path.of("ProjectTests/OneFileDirectory"));
        assertEquals(1, project.getRootNode().getChildren().size());
        assertEquals(Paths.get("ProjectTests/OneFileDirectory"), project.getRootNode().getPath());
    }

    @Test
    public void SimpleProjectTest()
    {
        Project project = projectManager.load(Path.of("ProjectTests/SimpleProject"));
        assertEquals(2, project.getRootNode().getChildren().size());
        assertEquals(Paths.get("ProjectTests/SimpleProject"), project.getRootNode().getPath());
        assertEquals(true, project.getRootNode().getChildren().get(1).isFolder());
        assertEquals(false, project.getRootNode().getChildren().get(1).getChildren().get(0).isFolder());
        assertEquals(false, project.getRootNode().getChildren().get(0).isFolder());
    }
}
