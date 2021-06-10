package feature;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.aspect.MavenAspect;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.project.AnyProject;
import fr.epita.assistants.ping.service.ProjectManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeTest {
    private ProjectManager projectManager;
    private Project project;


    @BeforeEach
    public void setUp(){
        Set aspects = new HashSet();
        aspects.add(new MavenAspect());
        project = new AnyProject(new FolderNode(Path.of("ProjectTests/SimpleProject"), null),aspects);
    }

    @Test
    public void BaseTest() {
        Feature.ExecutionReport report = project.getFeature(Mandatory.Features.Maven.TREE).get().execute(project, "tree_output.txt");
        assertEquals(true, report.isSuccess());
    }
}
