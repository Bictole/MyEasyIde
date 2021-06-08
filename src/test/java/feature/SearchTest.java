package feature;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchTest {

    private ProjectManager projectManager;
    private Project project;


    @BeforeEach
    public void setUp(){
        projectManager = new ProjectManager();
        project = projectManager.load(Path.of("ProjectTests/SearchProject"));
    }

    @Test
    public void NotFoundTest() {
        Feature.ExecutionReport report = projectManager.execute(project, Mandatory.Features.Any.SEARCH, "blabla");
        assertEquals(false, report.isSuccess());
    }
    @Test
    public void FoundTest() {
        Feature.ExecutionReport report = projectManager.execute(project, Mandatory.Features.Any.SEARCH, "test");
        assertEquals(true, report.isSuccess());
    }
}
