package feature;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistTest {
    private ProjectManager projectManager;
    private Project project;


    @BeforeEach
    public void setUp() {
        projectManager = new ProjectManager();
        project = projectManager.load(Path.of("ProjectTests/DistProject"));
    }

    @Test
    public void BasicTest() {
        Feature.ExecutionReport report = projectManager.execute(project, Mandatory.Features.Any.DIST);
        assertEquals(true, report.isSuccess());
    }
}
